package com.hcmus.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.hcmus.data.model.Album
import com.hcmus.ui.display.MediaFile

class CloudFirestoreService {
  companion object {
    const val DEFAULT_COLLECTION = "android-collection"
  }

  val db = Firebase.firestore.collection(DEFAULT_COLLECTION)

  fun addImages(email: String, data: List<MediaFile>) {
    db.document(email).set(mapOf("images" to data))
  }
}