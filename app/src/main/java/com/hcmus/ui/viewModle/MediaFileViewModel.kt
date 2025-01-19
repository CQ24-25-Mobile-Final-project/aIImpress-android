package com.hcmus.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hcmus.data.MediaFileDatabase
import com.hcmus.data.MediaFileRepository
import com.hcmus.ui.display.MediaFile
import kotlinx.coroutines.launch

class MediaFileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MediaFileRepository

    init {
        val mediaFileDao = MediaFileDatabase.getDatabase(application).mediaFileDao()
        repository = MediaFileRepository(mediaFileDao)
    }

    fun insert(mediaFile: MediaFile) = viewModelScope.launch {
        repository.insert(mediaFile)
    }

    suspend fun getAllMediaFiles() : List<MediaFile> {
        return repository.getAllMediaFiles()
    }

    fun updateTag(uri: String, tag: String) = viewModelScope.launch {
        repository.updateTag(uri, tag)
    }

    suspend fun getTagByUri(uri: String): String? {
        return repository.getTagByUri(uri)
    }

    fun removeTag(uri: String) = viewModelScope.launch {
        repository.removeTag(uri)
    }
}