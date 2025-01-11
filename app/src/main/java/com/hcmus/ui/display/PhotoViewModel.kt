package com.hcmus.ui.display

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hcmus.data.MediaFileDao
import com.hcmus.data.StorageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class PhotoViewModel(private val context: Context) : ViewModel() {
    private val _photos = MutableLiveData<List<MediaFile>>()
    val photos: LiveData<List<MediaFile>> = _photos

    private val storageService = StorageService(context)

    init {
        loadAllMediaFiles()
    }

    private fun loadAllMediaFiles() {
        val mediaReader = MediaReader(context)
        val allMediaFiles: List<MediaFile> = mediaReader.loadMediaFilesFromStorage()
        setPhotos(allMediaFiles)
    }

    fun setPhotos(mediaFiles: List<MediaFile>) {
        Log.d("PhotoViewModel", "Setting photos: $mediaFiles")
        _photos.value = mediaFiles
    }

    fun getPhotoByUri(photoUri: String): MediaFile? {
        return _photos.value?.find { it.uri.toString() == photoUri }
    }

    fun addTag(photoUri: String, tag: String) {
        Log.d("PhotoViewModel", "Adding tag: $tag to photo with URI: $photoUri")
        val updatedPhoto = _photos.value?.find { it.uri.toString() == photoUri }?.copy(tag = tag)
        Log.d("PhotoViewModel", "Updated photo: $updatedPhoto")
        updatedPhoto?.let {
            updatePhotoInFirebase(it)
        }
    }

    private fun updatePhotoInFirebase(photo: MediaFile) {
        storageService.upload(photo)?.addOnSuccessListener {
            Log.d("PhotoViewModel", "Updated photo uploaded successfully: ${photo.name}")
        }?.addOnFailureListener { exception ->
            Log.e("PhotoViewModel", "Failed to upload photo: ${exception.message}")
        }
    }
}

