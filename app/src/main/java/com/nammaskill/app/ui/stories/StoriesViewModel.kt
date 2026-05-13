package com.nammaskill.app.ui.stories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammaskill.app.data.model.SuccessStory
import com.nammaskill.app.data.repository.FirebaseRepository
import kotlinx.coroutines.launch

class StoriesViewModel : ViewModel() {
    private val _allStories = MutableLiveData<List<SuccessStory>>()
    private val _filteredStories = MutableLiveData<List<SuccessStory>>()
    val filteredStories: LiveData<List<SuccessStory>> = _filteredStories

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    var selectedDistrict = "All"
    var selectedTrade = "All"

    init { loadStories() }

    fun loadStories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val stories = FirebaseRepository.getSuccessStories()
                _allStories.value = stories
                applyFilters()
            } catch (e: Exception) {
                _filteredStories.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterByDistrict(district: String) { selectedDistrict = district; applyFilters() }
    fun filterByTrade(trade: String) { selectedTrade = trade; applyFilters() }

    private fun applyFilters() {
        val stories = _allStories.value ?: return
        _filteredStories.value = stories.filter { s ->
            (selectedDistrict == "All" || s.district == selectedDistrict) &&
            (selectedTrade == "All" || s.trade == selectedTrade)
        }
    }
}
