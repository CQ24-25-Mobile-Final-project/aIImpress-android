package com.hcmus.ui.album

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun ImagePickerScreen(context: Context) {
    val images = remember { mutableStateOf<List<Uri>>(emptyList()) }

    RequestMediaPermissions {
        images.value = fetchImages(context)
    }
    // Log the number of images loaded
    LaunchedEffect(images.value) {
        Log.d("ImagePickerScreen", "Images loaded: ${images.value.size}")
    }

    if (images.value.isNotEmpty()) {
        ImageGrid(images = images.value)
    } else {
        Text("No images available or permission not granted")
    }
}