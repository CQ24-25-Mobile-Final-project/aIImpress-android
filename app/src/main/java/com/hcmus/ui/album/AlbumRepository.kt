package com.hcmus.ui.album

import android.net.Uri

object AlbumRepository {
    private val _albums = mutableListOf<Pair<String, List<Uri>>>()
    val albums: List<Pair<String, List<Uri>>> get() = _albums

    private var _albumName: String? = null
    val albumName: String
        get() = _albumName ?: throw IllegalStateException("Album name has not been initialized")

    fun addAlbum(name: String, photos: List<Uri>) {
        val existingAlbumIndex = _albums.indexOfFirst { it.first == name }
        if (existingAlbumIndex != -1) {
            val updatedPhotos = _albums[existingAlbumIndex].second + photos
            _albums[existingAlbumIndex] = name to updatedPhotos
        } else {
            _albums.add(name to photos)
        }
    }

    fun createDefaultFavoriteAlbum() {
        if (_albums.none { it.first == "Favorite" }) {
            addAlbum("Favorite", emptyList())
        }
    }

    fun selectedAlbum(name: String): List<Uri> {
        _albumName = name
        return _albums.find { it.first == name }?.second ?: emptyList()
    }

    fun insertIntoAlbum(name: String, photos: List<Uri>) {
        val existingAlbumIndex = _albums.indexOfFirst { it.first == name }
        if (existingAlbumIndex != -1) {
            val updatedPhotos = _albums[existingAlbumIndex].second + photos
            _albums[existingAlbumIndex] = name to updatedPhotos
        }
    }

    fun deleteAlbum(name: String) {
        _albums.removeAll { it.first == name }
    }

    fun deletePhotoInAlbum(albumName: String, photoUri: Uri) {
        val albumIndex = _albums.indexOfFirst { it.first == albumName }
        if (albumIndex != -1) {
            val updatedPhotos = _albums[albumIndex].second.toMutableList()
            updatedPhotos.remove(photoUri)

            _albums[albumIndex] = albumName to updatedPhotos
        } else {
            throw IllegalArgumentException("Album not found: $albumName")
        }
    }

    fun addAlbumName(name: String) {
        _albumName = name
    }

    // Sort albums by name
    fun sortAlbumsByName(): List<Pair<String, List<Uri>>> {
        return _albums.sortedBy { it.first }
    }

    // Sort albums by the number of photos
    fun sortAlbumsByPhotoCount(): List<Pair<String, List<Uri>>> {
        return _albums.sortedByDescending { it.second.size }
    }
}
