package com.hcmus.ui.display


import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore

fun getAllPhotoPaths(context: Context): List<Uri> {
    val photoUris = mutableListOf<Uri>()

    val projection = arrayOf(MediaStore.Images.Media._ID) // Cột chứa ID của ảnh
    val cursor = context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // URI của bộ sưu tập ảnh
        projection,
        null, // Không lọc điều kiện
        null, // Không tham số bổ sung
        "${MediaStore.Images.Media.DATE_TAKEN} DESC"
    )

    cursor?.use {
        val idColumnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        while (it.moveToNext()) {
            val imageId = it.getLong(idColumnIndex)
            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId)
            photoUris.add(imageUri)
        }
    }

    return photoUris
}
