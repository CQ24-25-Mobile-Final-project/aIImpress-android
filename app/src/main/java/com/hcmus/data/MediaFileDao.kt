package com.hcmus.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hcmus.ui.display.MediaFile

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