package com.hcmus

import android.net.Uri

data class MediaFile(
    val uri: Uri,
    val name: String,
    val dateAdded: Long
)
