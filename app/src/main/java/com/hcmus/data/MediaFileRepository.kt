package com.hcmus.data

import android.util.Log
import com.hcmus.ui.display.MediaFile

class MediaFileRepository(private val mediaFileDao: MediaFileDao) {

    suspend fun insert(mediaFile: MediaFile) {
         val count = mediaFileDao.countByUri(mediaFile.uri.toString())

         if (count == 0) {
             mediaFileDao.insert(mediaFile)
         }
    }

    suspend fun getAllMediaFiles(): List<MediaFile> {
        return mediaFileDao.getAllMediaFiles()
    }

    suspend fun getTagByUri(uri: String): String? {
        return mediaFileDao.getTagByUri(uri)
    }

    suspend fun updateTag(uri: String, tag: String) {
        try {
            val currentTag = mediaFileDao.getTagByUri(uri)
            Log.d("MediaFileRepository", "Before update - URI: $uri, Current Tag: $currentTag")

            mediaFileDao.updateTag(uri, tag)

            val updatedTag = mediaFileDao.getTagByUri(uri)
            Log.d("MediaFileRepository", "After update - URI: $uri, Updated Tag: $updatedTag")
        } catch (e: Exception) {
            Log.e("MediaFileRepository", "Error updating tag for URI: $uri. Exception: ${e.message}")
        }
    }

    suspend fun removeTag(uri: String) {
        mediaFileDao.removeTag(uri)
    }
}