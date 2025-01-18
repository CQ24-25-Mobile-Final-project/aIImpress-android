package com.hcmus.data

import android.util.Log
import com.hcmus.ui.display.MediaFile
import androidx.lifecycle.LiveData
import com.hcmus.data.model.Profile
import com.hcmus.data.model.Gender

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

class ProfileRepository(private val profileDao: ProfileDao) {

    fun getAllProfiles(): LiveData<List<Profile>> {
        return profileDao.getAllProfiles()
    }

    suspend fun insert(profile: Profile) {
        profileDao.insert(profile)
    }

    suspend fun update(email: String, fullName: String, phoneNumber: String, country: String, gender: Gender, avatarUrl: String?) {
        profileDao.update(email, fullName, phoneNumber, country, gender, avatarUrl)
    }

    suspend fun delete(profile: Profile) {
        profileDao.delete(profile)
    }

    suspend fun getProfileById(id: Int): Profile? {
        return profileDao.getProfileById(id)
    }

    suspend fun getProfileByEmail(email: String): Profile? {
        return profileDao.getProfileByEmail(email)
    }
}