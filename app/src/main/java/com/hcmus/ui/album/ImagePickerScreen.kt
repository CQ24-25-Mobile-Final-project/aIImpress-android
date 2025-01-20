package com.hcmus.ui.album

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.hcmus.ui.secret.ImageGrid
import com.hcmus.ui.secret.RequestMediaPermissions
import com.hcmus.ui.secret.fetchImages

@Composable
fun ImagePickerScreen(context: Context) {
    val images = remember { mutableStateOf<List<Uri>>(emptyList()) }

    RequestMediaPermissions {
        images.value = fetchImages(context)
    }

    if (images.value.isNotEmpty()) {
        ImageGrid(images = images.value)
    } else {
        Text("No images available or permission not granted")
    }
}