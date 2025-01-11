package com.hcmus.data.firestore

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.hcmus.data.model.Album


class AlbumFirestoreService {
    private val db = FirebaseFirestore.getInstance()

    private val _albums = MutableLiveData<List<Album>>()
    val albums: LiveData<List<Album>> get() = _albums

    private var _albumName: String? = null
    val albumName: String
        get() = _albumName ?: throw IllegalStateException("Album name has not been initialized")

    // Lưu album vào Firestore
    fun saveAlbum(email: String, album: Album) {
        db.collection("android-collection")
            .document(email)
            .collection("albums")
            .document(album.id.toString())
            .set(album)
            .addOnSuccessListener {
                Log.d("Firestore", "Album saved successfully!")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error saving album: ${e.message}")
            }
    }

    // Lấy danh sách album từ Firestore
    fun getAlbums(email: String, callback: (List<Album>) -> Unit) {
        db.collection("android-collection")
            .document(email)
            .collection("albums")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val albums = querySnapshot.documents.mapNotNull { it.toObject(Album::class.java) }
                callback(albums)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error getting albums: ${e.message}")
                callback(emptyList())
            }
    }


    // Thêm ảnh vào album
    fun addPhotosToAlbum(email: String, albumName: String, photos: List<Uri>,onComplete: () -> Unit) {
        db.collection("android-collection")
            .document(email)
            .collection("albums")
            .whereEqualTo("name", albumName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val document = querySnapshot.documents.firstOrNull()
                if (document != null) {
                    val album = document.toObject(Album::class.java)
                    val updatedPhotos = (album?.images ?: emptyList()) + photos

                    db.collection("android-collection")
                        .document(email)
                        .collection("albums")
                        .document(document.id)
                        .update("images", updatedPhotos)
                        .addOnSuccessListener {
                            Log.d("Firestore", "Photos added to album successfully!")
                            onComplete()
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error adding photos to album: ${e.message}")
                        }
                } else {
                    Log.e("Firestore", "Album not found: $albumName")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching album: ${e.message}")
            }
    }


    // Xóa album khỏi Firestore
    fun deleteAlbum(email: String, albumId: String) {
        db.collection("android-collection")
            .document(email)
            .collection("albums")
            .document(albumId)
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Album deleted successfully!")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error deleting album: ${e.message}")
            }
    }

    // Xóa ảnh khỏi album trong Firestore
    fun deletePhotoFromAlbum(email: String, albumId: String, photoUri: Uri) {
        db.collection("android-collection")
            .document(email)
            .collection("albums")
            .document(albumId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val currentAlbum = document.toObject(Album::class.java)
                    val updatedImages = currentAlbum?.images?.filter { it != photoUri.toString()} ?: emptyList()

                    db.collection("android-collection")
                        .document(email)
                        .collection("albums")
                        .document(albumId)
                        .update("images", updatedImages)
                        .addOnSuccessListener {
                            Log.d("Firestore", "Photo removed from album successfully!")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error removing photo from album: ${e.message}")
                        }
                } else {
                    Log.e("Firestore", "Album not found: $albumId")
                }
            }
    }

    // Đổi tên album trong Firestore
    fun renameAlbum(email: String, albumId: String, newName: String, callback: (Boolean) -> Unit) {
        db.collection("android-collection")
            .document(email)
            .collection("albums")
            .document(albumId)
            .update("name", newName)
            .addOnSuccessListener {
                Log.d("Firestore", "Album renamed successfully!")
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error renaming album: ${e.message}")
                callback(false)
            }
    }


}

