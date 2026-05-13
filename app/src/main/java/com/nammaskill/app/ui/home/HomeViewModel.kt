package com.nammaskill.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammaskill.app.data.model.Course
import com.nammaskill.app.data.repository.FirebaseRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _allCourses = MutableLiveData<List<Course>>()
    private val _filteredCourses = MutableLiveData<List<Course>>()
    val filteredCourses: LiveData<List<Course>> = _filteredCourses

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Filter state
    var selectedTrade = "All"
    var selectedDuration = "All"
    var jobGuaranteedOnly = false
    var currentSearchQuery = ""

    init {
        loadCourses()
    }

    fun loadCourses() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val courses = FirebaseRepository.getCourses()
                _allCourses.value = courses
                applyFilters()
            } catch (e: Exception) {
                _error.value = "Failed to load courses. Check your connection."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterByTrade(trade: String) {
        selectedTrade = trade
        applyFilters()
    }

    fun filterByDuration(durationType: String) {
        selectedDuration = durationType
        applyFilters()
    }

    fun filterByJobGuaranteed(onlyJobGuaranteed: Boolean) {
        jobGuaranteedOnly = onlyJobGuaranteed
        applyFilters()
    }

    fun setSearchQuery(query: String) {
        currentSearchQuery = query.lowercase()
        applyFilters()
    }

    private fun applyFilters() {
        val courses = _allCourses.value ?: return
        val filtered = courses.filter { course ->
            val tradeMatch = selectedTrade == "All" || course.trade == selectedTrade
            val durationMatch = selectedDuration == "All" || course.durationType == selectedDuration
            val jobMatch = !jobGuaranteedOnly || course.jobGuaranteed
            val searchMatch = currentSearchQuery.isEmpty() || 
                    course.title.lowercase().contains(currentSearchQuery) ||
                    course.trade.lowercase().contains(currentSearchQuery) ||
                    course.centerName.lowercase().contains(currentSearchQuery)
            
            tradeMatch && durationMatch && jobMatch && searchMatch
        }
        _filteredCourses.value = filtered
    }
}
