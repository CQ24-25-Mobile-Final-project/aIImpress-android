package com.hcmus.ui.album

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AlbumViewModel : ViewModel() {
    private val _albums = MutableLiveData<List<Pair<String, List<Uri>>>>()
    val albums: LiveData<List<Pair<String, List<Uri>>>> get() = _albums

    private val _albumName = MutableLiveData<String>()
    val albumName: LiveData<String>  get()= _albumName

    private val _photos = MutableLiveData<List<Uri>>()
    val photos: LiveData<List<Uri>> = _photos

    private val albumRepository = AlbumRepository

    init {
        _albums.value = albumRepository.albums
        AlbumRepository.createDefaultFavoriteAlbum()
    }

    fun selectAlbum(name: String) {
        _albumName.value = name
        _photos.value = albumRepository.selectedAlbum(name)
    }

    fun addAlbum(name: String, photos: List<Uri>) {
        albumRepository.addAlbum(name, photos)
        _albums.value = albumRepository.albums
    }

    fun insertIntoAlbum(name: String, photos: List<Uri>) {
        albumRepository.insertIntoAlbum(name, photos)
        _albums.value = albumRepository.albums
    }

    fun deleteAlbum(name: String) {
        albumRepository.deleteAlbum(name)
        _albums.value = albumRepository.albums
    }

    fun deletePhotoInAlbum(albumName: String, photoUri: Uri) {
        albumRepository.deletePhotoInAlbum(albumName, photoUri)
        _photos.value = albumRepository.selectedAlbum(albumName)
        _albums.value = albumRepository.albums
    }

    fun addAlbumName(name: String) {
        Log.d("AlbumViewModel", "Setting album name: $name")
        albumRepository.addAlbumName(name)
        _albums.value = albumRepository.albums
    }

    // Sort albums by name
    fun sortAlbumsByName() {
        _albums.value = albumRepository.sortAlbumsByName()
    }

    // Sort albums by the number of photos
    fun sortAlbumsByPhotoCount() {
        _albums.value = albumRepository.sortAlbumsByPhotoCount()
    }

    fun addToFavorite(photoUri: Uri) {
        insertIntoAlbum("Favorite", listOf(photoUri))
    }
}