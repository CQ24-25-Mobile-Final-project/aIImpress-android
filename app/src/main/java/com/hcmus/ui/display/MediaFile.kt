package com.hcmus.ui.display

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_files")
data class MediaFile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uri: Uri,
    var tag: String,
    val name: String,
    val dateAdded: Long,
    val url: Uri? = null
)
