package com.hcmus.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hcmus.data.model.Gender
import com.hcmus.ui.display.MediaFile
import com.hcmus.data.model.Profile

@Dao
interface MediaFileDao {
    @Insert
    suspend fun insert(mediaFile: MediaFile)

    @Query("SELECT * FROM media_files")
    suspend fun getAllMediaFiles(): List<MediaFile>

    @Query("SELECT COUNT(*) FROM media_files WHERE uri = :uri")
    suspend fun countByUri(uri: String): Int

    @Query("UPDATE media_files SET tag = :tag WHERE uri = :uri")
    suspend fun updateTag(uri: String, tag: String)

    @Query("SELECT tag FROM media_files WHERE uri = :uri")
    suspend fun getTagByUri(uri: String): String?
    
    @Query("UPDATE media_files SET tag = '' WHERE uri = :uri")
    suspend fun removeTag(uri: String)
}

@Dao
interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: Profile)

    @Query("UPDATE profiles SET fullName = :fullName, phoneNumber = :phoneNumber, country = :country, gender = :gender, avatarUrl = :avatarUrl WHERE email = :email")
    suspend fun update(email: String, fullName: String, phoneNumber: String, country: String, gender: Gender, avatarUrl: String?)
    
    @Delete
    suspend fun delete(profile: Profile)

    @Query("SELECT * FROM profiles WHERE id = :id")
    suspend fun getProfileById(id: Int): Profile?

    @Query("SELECT * FROM profiles WHERE email = :email")
    suspend fun getProfileByEmail(email: String): Profile?

    @Query("SELECT * FROM profiles")
    fun getAllProfiles(): LiveData<List<Profile>>
}