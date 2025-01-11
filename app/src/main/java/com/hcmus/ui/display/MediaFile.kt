package com.hcmus.ui.display

import android.net.Uri

data class MediaFile(
    val uri: Uri,
    var tag: String,
    val name: String,
    val dateAdded: Long,
    val url: Uri? = null
)
