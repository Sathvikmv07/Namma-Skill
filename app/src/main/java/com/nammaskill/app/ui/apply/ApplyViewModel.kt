package com.nammaskill.app.ui.apply

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammaskill.app.data.model.Application
import com.nammaskill.app.data.repository.FirebaseRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ApplyViewModel : ViewModel() {

    private val _submissionResult = MutableLiveData<Result<String>?>()
    val submissionResult: LiveData<Result<String>?> = _submissionResult

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun submitApplication(
        courseId: String,
        courseName: String,
        name: String,
        age: String,
        phone: String,
        village: String,
        district: String,
        education: String,
        experience: String,
        language: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val application = Application(
                    courseId = courseId,
                    courseName = courseName,
                    candidateName = name,
                    age = age.toIntOrNull() ?: 0,
                    phone = phone,
                    village = village,
                    district = district,
                    education = education,
                    experience = experience,
                    language = language,
                    appliedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                    status = "Submitted"
                )
                _submissionResult.value = FirebaseRepository.submitApplication(application)
            } catch (e: Exception) {
                _submissionResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
