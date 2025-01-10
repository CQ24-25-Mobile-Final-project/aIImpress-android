package com.hcmus.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.storage
import com.hcmus.ui.display.MediaFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.internal.wait
import java.io.File

class StorageService(val context: Context) {

  private val storageRef = Firebase.storage.reference

  fun upload(file: MediaFile): UploadTask? {
    val email = ContextStore.get(context, "email") ?: return null
    val riversRef = storageRef.child("$email/images/${file.name}")
    val uploadTask = riversRef.putFile(file.uri)
    return uploadTask
  }

  fun download(file: MediaFile): Uri? {
    val email = ContextStore.get(context, "email") ?: return null
    val ref = storageRef.child("$email/images/${file.name}")
    val localFile = File.createTempFile("images", "jpg")

    ref.getFile(localFile)

    return Uri.fromFile(localFile)
  }

  fun bulkUpload(
    files: List<MediaFile>,
    onCompleted: (List<MediaFile>) -> Unit
  ) {

    CoroutineScope(Dispatchers.IO).launch {
      val email = ContextStore.get(context, "email")

      val urls = files.map { file ->
        upload(file)?.await()
        storageRef.child("${email}/images/${file.name}").downloadUrl.await()
      }

      onCompleted(files.mapIndexed { index, file ->
        file.copy(url = urls[index])
      })
    }
  }

  fun bulkDownload(files: List<MediaFile>) {
    files.forEach { file ->
      CoroutineScope(Dispatchers.IO).launch {
        download(file)
      }
    }
  }
}