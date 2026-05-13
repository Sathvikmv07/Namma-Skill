package com.nammaskill.app.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammaskill.app.data.model.SkillCenter
import com.nammaskill.app.data.repository.FirebaseRepository
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    private val _centers = MutableLiveData<List<SkillCenter>>()
    val centers: LiveData<List<SkillCenter>> = _centers

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init { loadCenters() }

    fun loadCenters() {
        viewModelScope.launch {
            try {
                _centers.value = FirebaseRepository.getSkillCenters()
            } catch (e: Exception) {
                _error.value = "Could not load skill centers."
            }
        }
    }
}
