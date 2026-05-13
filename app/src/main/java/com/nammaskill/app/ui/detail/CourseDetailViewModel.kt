package com.nammaskill.app.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammaskill.app.data.model.Course
import com.nammaskill.app.data.repository.FirebaseRepository
import kotlinx.coroutines.launch

class CourseDetailViewModel : ViewModel() {

    private val _course = MutableLiveData<Course?>()
    val course: LiveData<Course?> = _course

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _callbackResult = MutableLiveData<Boolean?>()
    val callbackResult: LiveData<Boolean?> = _callbackResult

    fun loadCourse(courseId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _course.value = FirebaseRepository.getCourseById(courseId)
            } catch (e: Exception) {
                _error.value = "Failed to load course details."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun requestCallback(courseId: String, phone: String, name: String) {
        viewModelScope.launch {
            val result = FirebaseRepository.saveCallbackRequest(courseId, phone, name)
            _callbackResult.value = result.isSuccess
        }
    }
}
