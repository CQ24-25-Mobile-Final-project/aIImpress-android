package com.hcmus.ui.secret

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hcmus.data.firestore.AlbumFirestoreService
import com.hcmus.data.model.Album

class AlbumModel : ViewModel() {
    private val albumService = AlbumFirestoreService()
    private val albumRepository = Albums

    private val _albums = MutableLiveData<List<Album>>()
    val albums: LiveData<List<Album>> get() = _albums

    private val _albumName = MutableLiveData<String>()
    val albumName: LiveData<String>  get()= _albumName

    private val _photos = MutableLiveData<List<Uri>>()
    val photos: LiveData<List<Uri>> = _photos

    private val _selectedAlbum = MutableLiveData<Album?>()
    val selectedAlbum: LiveData<Album?> get() = _selectedAlbum

    private val _selectedPhotos = MutableLiveData<List<Uri>>() // Danh sách ảnh được chọn
    val selectedPhotos: LiveData<List<Uri>> get() = _selectedPhotos

    fun updateSelectedPhotos(photos: List<Uri>) {
        _selectedPhotos.value = photos
    }


    fun fetchAlbums(email: String) {
        albumService.getAlbums(email) { albumList ->
            val defaultAlbumExists = albumList.any { it.name == "defaultVault" }
            if (!defaultAlbumExists) {
                // Nếu album mặc định chưa tồn tại, lưu vào Firestore
                val defaultAlbum = Album(name = "defaultVault", images = emptyList())
                albumService.saveAlbum(email, defaultAlbum)
                // Cập nhật danh sách albums sau khi lưu
                albumService.getAlbums(email) { updatedAlbumList ->
                    val updatedList = (listOf(defaultAlbum) + updatedAlbumList)
                        .distinctBy { it.name }
                    _albums.postValue(updatedList)
                }

            } else {
                // Nếu đã tồn tại, chỉ cập nhật danh sách albums
                val updatedList = (listOf(Album(name = "defaultVault")) + albumList)
                    .distinctBy { it.name }
                _albums.postValue(updatedList)
            }
        }
    }

    fun deletePhotoInAlbum(albumName: String, photoUri: Uri) {
        albumRepository.deletePhotoInAlbum(albumName, photoUri)
        _photos.value = albumRepository.selectedAlbum(albumName)
        _albums.value = albumRepository.albums
    }


    // Thêm album mới
    fun addAlbum(email: String, albumName: String) {
        val newAlbum = Album(name = albumName, images = emptyList())
        albumService.saveAlbum(email, newAlbum)
        fetchAlbums(email) // Cập nhật danh sách sau khi thêm
    }

    // Lưu album mới vào Firestore
    fun saveAlbum(email: String, name: String, photos: List<Uri>) {
        val photoStrings = photos.map { it.toString() }
        val album = Album(
            name = name,
            images = photoStrings
        )
        albumService.saveAlbum(email, album)
        fetchAlbums(email) // Cập nhật danh sách albums
    }


    // Đổi tên album
    fun renameAlbum(email: String, oldName: String, newName: String) {
        albumService.renameAlbum(email, oldName, newName) {
            fetchAlbums(email) // Cập nhật danh sách sau khi đổi tên
        }
    }


    // Thêm ảnh vào album
    fun addPhotosToAlbum(email: String, albumName: String, photos: List<Uri>) {
        albumService.addPhotosToAlbum(email, albumName, photos) {
            fetchAlbums(email) // Gọi lại fetchAlbums sau khi thêm ảnh hoàn tất
        }
    }
    // Xóa album
    fun deleteAlbum(email: String, albumId: String) {
        albumService.deleteAlbum(email, albumId)
        fetchAlbums(email) // Cập nhật danh sách albums
    }

    // Xóa ảnh khỏi album
    fun deletePhotoFromAlbum(email: String, albumId: String, photoUri: Uri) {
        albumService.deletePhotoFromAlbum(email, albumId, photoUri)
        fetchAlbums(email) // Cập nhật danh sách albums
    }


    fun selectAlbum(name: String) {
        _albumName.value = name
        _photos.value = albumRepository.selectedAlbum(name)
        Log.d("AlbumModel", "Selected album: $name")
    }


}
