package com.nammaskill.app.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammaskill.app.data.model.Application
import com.nammaskill.app.data.repository.FirebaseRepository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val _applications = MutableLiveData<List<Application>>()
    val applications: LiveData<List<Application>> = _applications

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadApplications(phone: String) {
        if (phone.isEmpty()) { _applications.value = emptyList(); return }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _applications.value = FirebaseRepository.getUserApplications(phone)
            } catch (e: Exception) {
                _applications.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
