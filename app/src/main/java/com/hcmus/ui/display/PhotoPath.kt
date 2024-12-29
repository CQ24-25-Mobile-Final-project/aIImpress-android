package com.hcmus.ui.display


import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore

fun getAllPhotoPaths(context: Context): List<Uri> {
    val photoUris = mutableListOf<Uri>()
    val projection = arrayOf(
        MediaStore.Images.Media._ID // Truy cập ID thay vì đường dẫn trực tiếp
    )
    val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    // Truy vấn MediaStore
    context.contentResolver.query(
        uri,
        projection,
        null, // Không lọc, lấy tất cả ảnh
        null, // Không cần tham số lọc
        "${MediaStore.Images.Media.DATE_TAKEN} DESC" // Sắp xếp theo ngày
    )?.use { cursor ->
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(columnIndex)
            val contentUri = ContentUris.withAppendedId(uri, id)
            photoUris.add(contentUri) // Thêm URI vào danh sách
        }
    }

    return photoUris
}
