package com.hcmus.ui.display

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Downloads
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import java.io.InputStream

data class PhotoDetails(
    val dateTime: String?,
    val iso: String?,
    val aperture: String?,
    val exposureTime: String?,
    val focalLength: String?,
    val model: String?,
    val make: String?,
    val fileSize: String?,
    val screenShot: Boolean = false,
    var fromMedia: String? = null
)

fun getPhotoDetails(context: Context, photoUri: String): PhotoDetails? {
    try {
        val uri = Uri.parse(photoUri)

        // Use ContentResolver to get file details from MediaStore
        val cursor = context.contentResolver.query(
            uri,
            arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE),
            null,
            null,
            null
        )

        cursor?.use {
            // Ensure the cursor is not empty
            if (it.moveToFirst()) {
                val filePathIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
                val fileSizeIndex = it.getColumnIndex(MediaStore.Images.Media.SIZE)

                if (filePathIndex >= 0 && fileSizeIndex >= 0) {
                    val filePath = it.getString(filePathIndex)
                    val fileSize = it.getLong(fileSizeIndex)
                    Log.d("SmartAlbumOrganizer", "File Path: $filePath")

                    // Check if the file is a screenshot or from specific media
                    var media: String? = null
                    var screenShot = false

                    if (filePath.contains("Screenshots", true)) {
                        screenShot = true
                    } else if (filePath.contains("Zalo", true) || filePath.contains("Download/Zalo", true)) {
                        media = "Zalo"
                    } else if (filePath.contains("Messenger", true) || filePath.contains("Download/Messenger", true)) {
                        media = "Messenger"
                    } else if (filePath.contains("Camera", true)) {
                        media = "Camera"
                    } else if (filePath.contains("Restored", true)) {
                        media = "Restored"
                    }

                    // Open an InputStream to extract EXIF data
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        val exif = ExifInterface(inputStream)
                        val dateTime = exif.getAttribute(ExifInterface.TAG_DATETIME)
                        val iso = exif.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS)
                        val aperture = exif.getAttribute(ExifInterface.TAG_F_NUMBER)
                        val exposureTime = exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME)
                        val focalLength = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH)
                        val model = exif.getAttribute(ExifInterface.TAG_MODEL)
                        val make = exif.getAttribute(ExifInterface.TAG_MAKE)
                        val software = exif.getAttribute(ExifInterface.TAG_SOFTWARE)

                        return PhotoDetails(
                            dateTime = dateTime,
                            iso = iso,
                            aperture = aperture,
                            exposureTime = exposureTime,
                            focalLength = focalLength,
                            model = model,
                            make = make,
                            fileSize = "$fileSize bytes",
                            screenShot = screenShot,
                            fromMedia = media
                        )
                    }
                } else {
                    Log.e("SmartAlbumOrganizer", "Invalid column index for file path or file size.")
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun getPhotoDetails2(context: Context, photoUri: String): PhotoDetails? {
    try {
        val uri = Uri.parse(photoUri)
        val inputStream: InputStream? = context.contentResolver.openInputStream(Uri.parse(photoUri))
        inputStream?.use {
            val exif = ExifInterface(it)

            val dateTime = exif.getAttribute(ExifInterface.TAG_DATETIME)
            val iso = exif.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS)
            val aperture = exif.getAttribute(ExifInterface.TAG_F_NUMBER)
            val exposureTime = exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME)
            val focalLength = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH)
            val model = exif.getAttribute(ExifInterface.TAG_MODEL)
            val make = exif.getAttribute(ExifInterface.TAG_MAKE)
            val software = exif.getAttribute(ExifInterface.TAG_SOFTWARE)
            val fileSize = inputStream.available().toString() + " bytes"

            val filePath = Uri.parse(photoUri).path ?: "Unknown path"
            Log.d("SmartAlbumOrganizer", "File Path: $filePath")

            var media: String? = null
            var screenShot = false
            when {
                filePath.contains("Screenshots", true) -> {
                    screenShot = true
                }
                filePath.contains("Zalo", true) || filePath.contains("Download/Zalo", true) -> {
                    media = "Zalo"
                }
                filePath.contains("Messenger", true) || filePath.contains("Download/Messenger", true) -> {
                    media = "Messenger"
                }
            }

            if (software?.contains("Screenshot", true) == true || make?.contains("Android", true) == true) {
                screenShot = true
            }

            return PhotoDetails(
                dateTime = dateTime,
                iso = iso,
                aperture = aperture,
                exposureTime = exposureTime,
                focalLength = focalLength,
                model = model,
                make = make,
                fileSize = fileSize,
                screenShot = screenShot,
                fromMedia = media
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("SmartAlbumOrganizer", "Error getting photo details for URI: $photoUri", e)
    }
    return null
}