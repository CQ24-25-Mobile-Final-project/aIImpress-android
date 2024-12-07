package com.example.photoapp

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import android.os.Build
import java.text.SimpleDateFormat
import java.util.*

class MediaReader(private val context: Context) {

    fun getAllMediaFiles(): Map<String, List<MediaFile>> {
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

                mediaFiles.add(MediaFile(uri = uri, name = name, dateAdded = dateAdded))
            }
        }

        // Định dạng ngày từ timestamp
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        // Nhóm ảnh theo ngày
        return mediaFiles.groupBy { mediaFile ->
            val date = Date(mediaFile.dateAdded * 1000L) // Chuyển timestamp từ giây sang milli giây
            dateFormat.format(date)
        }
    }
}
