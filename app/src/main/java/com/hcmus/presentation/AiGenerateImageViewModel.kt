package com.hcmus.presentation

import android.util.Log
import com.hcmus.domain.Constants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hcmus.data.ImageRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class AiGenerateImageViewModel(private val repository: ImageRepository) : ViewModel() {
    private val _imageUrl = MutableStateFlow<String?>(null)
    val imageUrl: StateFlow<String?> = _imageUrl

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun generateImage(prompt: String) {
        viewModelScope.launch {
            _imageUrl.value = Constants.LOADING
            Log.d(Constants.TAG, "GENERATING IMAGE with prompt: $prompt")
            val result = repository.generateImage(prompt)
            result
                .onSuccess { imgUrl ->
                    _imageUrl.value = imgUrl
                    Log.d(Constants.TAG, "Image generated successfully: $imgUrl")
                    _error.value = null
                }
                .onFailure { throwable ->
                    Log.e(Constants.TAG, "Error generating image: ${throwable.message}")
                    setError(throwable.message ?: "Unknown error occurred")
                }
        }
    }

    fun setError(message: String) {
        _error.value = message
    }
}