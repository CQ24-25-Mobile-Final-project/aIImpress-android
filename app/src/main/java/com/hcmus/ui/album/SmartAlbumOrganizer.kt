package com.hcmus.utils

import android.content.Context
import android.net.Uri
import com.hcmus.ui.display.getPhotoDetails
import com.hcmus.ui.album.AlbumViewModel

fun SmartAlbumOrganizer(photoUri: String, context: Context, albumViewModel: AlbumViewModel) {
    val photoDetails = getPhotoDetails(context, photoUri)

    photoDetails?.let { details ->
        when {
            details.screenShot -> {
                albumViewModel.addAlbum("Screenshots", listOf(Uri.parse(photoUri)))
            }
            details.fromMedia == "Zalo" -> albumViewModel.addAlbum("Zalo", listOf(Uri.parse(photoUri)))
            details.fromMedia == "Messenger" -> albumViewModel.addAlbum("Messenger", listOf(Uri.parse(photoUri)))
            details.fromMedia == "Camera" -> albumViewModel.addAlbum("Camera", listOf(Uri.parse(photoUri)))
            details.fromMedia == "Restored" -> albumViewModel.addAlbum("Restored", listOf(Uri.parse(photoUri)))
        }
    }
}
