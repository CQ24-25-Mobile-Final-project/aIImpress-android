package com.hcmus.ui.display

import android.content.Context
import android.net.Uri
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
    val fileSize: String?
)

fun getPhotoDetails(context: Context, photoUri: String): PhotoDetails? {
    try {
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
            val fileSize = inputStream.available().toString() + " bytes"

            return PhotoDetails(
                dateTime = dateTime,
                iso = iso,
                aperture = aperture,
                exposureTime = exposureTime,
                focalLength = focalLength,
                model = model,
                make = make,
                fileSize = fileSize
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}