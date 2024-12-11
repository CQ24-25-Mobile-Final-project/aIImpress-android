package com.hcmus.ui.components

import android.net.Uri

data class MediaFile(
    val uri: Uri,
    val name: String,
    val dateAdded: Long
)
