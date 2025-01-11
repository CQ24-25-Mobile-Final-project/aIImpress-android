package com.hcmus.ui.components

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.hcmus.data.StorageService
import com.hcmus.ui.display.MediaFile

@Composable
fun addTag1(tagName: String, photoUri: String, mediaFileMap: Map<String, List<MediaFile>> ) {
    var photoDetail = mediaFileMap.values.flatten().find { it.uri.toString() == photoUri }

    // Ghi log chi tiết ảnh
    photoDetail?.let {
        it.tag = tagName

    } ?: Log.d("Photo Details", "Không tìm thấy ảnh với URI: $photoUri")
    val storageService = StorageService(LocalContext.current)
    photoDetail?.let { storageService.upload(it) }
    if (photoDetail != null) {
        Log.d("Photo Details", "URI: ${photoDetail.uri}, Tag: ${photoDetail.tag}, Name: ${photoDetail.name}, Date Added: ${photoDetail.dateAdded}")
    }
}

fun getPhotoDetail(photoUri: String): MediaFile? {
    val mediaFileMap = MediaFileManager.getMediaFileMap() // Giả sử bạn có một lớp quản lý MediaFile
    return mediaFileMap.values.flatten().find { it.uri.toString() == photoUri }
}

object MediaFileManager {
    private val mediaFileMap: Map<String, List<MediaFile>> = mutableMapOf()

    fun setMediaFileMap(map: Map<String, List<MediaFile>>) {
        (mediaFileMap as MutableMap).putAll(map)
    }

    fun getMediaFileMap(): Map<String, List<MediaFile>> {
        return mediaFileMap
    }
}


