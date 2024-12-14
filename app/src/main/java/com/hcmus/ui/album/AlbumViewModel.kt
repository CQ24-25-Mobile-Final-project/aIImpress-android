package com.hcmus.ui.album

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf

object AlbumRepository {
    private val _albums = mutableStateListOf<Pair<String, List<Uri>>>()
    val albums: List<Pair<String, List<Uri>>> get() = _albums

    private var _albumName: String? = null
    val albumName: String
        get() = _albumName ?: throw IllegalStateException("Album name has not been initialized")

    fun addAlbum(name: String, photos: List<Uri>) {
        _albums.add(name to photos)
    }

    fun insertIntoAlbum(name: String, photos: List<Uri>) {
        val existingAlbumIndex = _albums.indexOfFirst { it.first == name }
        if (existingAlbumIndex != -1) {
            val updatedPhotos = _albums[existingAlbumIndex].second + photos
            _albums[existingAlbumIndex] = name to updatedPhotos
        }
    }

    fun deleteAlbum(name: String) {
        val iterator = _albums.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().first == name) {
                iterator.remove()
            }
        }
    }


    fun addAlbumName(name: String) {
        _albumName = name
    }
}