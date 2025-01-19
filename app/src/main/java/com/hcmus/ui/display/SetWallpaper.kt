package com.hcmus.ui.display

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import java.io.IOException

fun setAsWallpaper(context: Context, photoUri: String) {
    try {
        val wallpaperManager = WallpaperManager.getInstance(context)

        val inputStream = context.contentResolver.openInputStream(Uri.parse(photoUri))
        if (inputStream != null) {
            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)


            wallpaperManager.setBitmap(bitmap)
            Toast.makeText(context, "Wallpaper set successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Invalid image URI", Toast.LENGTH_SHORT).show()
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to set wallpaper", Toast.LENGTH_SHORT).show()
    }
}
