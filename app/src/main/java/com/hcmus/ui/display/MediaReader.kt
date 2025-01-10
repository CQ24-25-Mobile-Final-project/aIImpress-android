package com.hcmus.ui.display

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.os.Build
import android.util.Log
import com.hcmus.data.CloudFirestoreService
import com.hcmus.data.ContextStore
import com.hcmus.data.StorageService
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class MediaReader(
  private val context: Context
) {
  private val storageService: StorageService = StorageService(context)
  private val cloudFirestoreService: CloudFirestoreService = CloudFirestoreService()

  fun getAllMediaFiles(): Map<String, List<MediaFile>> {
    val email = ContextStore.get(context, "email")
    if (email.isNullOrEmpty()) {
      Log.e("MediaReader", "Email not found in ContextStore")
      return emptyMap()
    }

    Log.d("MediaReader", "Fetching media files for email: $email")
    val mediaFiles = mutableListOf<MediaFile>()
    val serverFiles = loadMediaFilesFromStorage()
    val localFiles = loadMediaFilesFromDevice()

    val fileNotInServer = localFiles.filter { localFile ->
      serverFiles.none { it.uri == localFile.uri }
    }
    if (fileNotInServer.isNotEmpty()) {
      storageService.bulkUpload(fileNotInServer) { response ->
        cloudFirestoreService.addImages(email, response)
      }
    }

    val fileNotInDevice = serverFiles.filter { serverFile ->
      localFiles.none { it.uri == serverFile.uri }
    }
    if (fileNotInDevice.isNotEmpty()) {
      storageService.bulkDownload(fileNotInDevice)
    }

    mediaFiles.addAll(localFiles + serverFiles)

    val distinctFiles = mediaFiles.distinctBy { it.uri }

    return distinctFiles
      .groupBy { mediaFile ->
        val date = Date(mediaFile.dateAdded * 1000L)
        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date)
      }
  }

  fun loadMediaFilesFromDevice(): List<MediaFile> {
    val mediaFiles = mutableListOf<MediaFile>()

    val queryUri = if (Build.VERSION.SDK_INT >= 29) {
      MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
      MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    val projection = arrayOf(
      MediaStore.Images.Media._ID,
      MediaStore.Images.Media.DISPLAY_NAME,
      MediaStore.Images.Media.DATE_ADDED
    )

    context.contentResolver.query(
      queryUri, projection, null, null, "${MediaStore.Images.Media.DATE_ADDED} DESC"
    )?.use { cursor ->
      val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
      val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
      val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

      while (cursor.moveToNext()) {
        val id = cursor.getLong(idColumn)
        val name = cursor.getString(nameColumn)
        val dateAdded = cursor.getLong(dateColumn)
        val uri = ContentUris.withAppendedId(queryUri, id)

        mediaFiles.add(
          MediaFile(
            uri = uri,
            name = name,
            dateAdded = dateAdded,
          )
        )
      }
    }

    return mediaFiles.toList()
  }

  fun loadMediaFilesFromStorage(): List<MediaFile> {
    val mediaFiles = mutableListOf<MediaFile>()
    val email = ContextStore.get(context, "email") ?: throw Exception("Email not found")

    runBlocking {
      val document = cloudFirestoreService
        .db
        .document(email)
        .get()
        .await()
        .data?.get("images") ?: return@runBlocking

      val list = document as List<Map<String, Any>>
      mediaFiles.addAll(list.map {
        MediaFile(
          uri = Uri.parse(it["uri"] as String),
          url = Uri.parse(it["url"] as String),
          name = it["name"] as String,
          dateAdded = it["dateAdded"] as Long
        )
      })
    }

    return mediaFiles
  }
}
