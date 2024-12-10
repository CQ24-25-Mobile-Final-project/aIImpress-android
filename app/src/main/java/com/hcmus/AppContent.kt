package com.hcmus

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import java.io.File

object Constants {
    const val APPLICATION_ID = "com.example.com.hcmus"
}

@Composable
fun AppContent(navController: NavController) {
    val context = LocalContext.current

    // Create temporary file for the photo
    val file = context.createImageFile()

    // URI for temporary photo file
    val uri = FileProvider.getUriForFile(
        context,
        "${Constants.APPLICATION_ID}.provider",
        file
    )

    var capturedImageUri by remember { mutableStateOf<Uri>(Uri.EMPTY) }

    // Launcher for taking a picture
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            // Save the image to the gallery
            val savedUri = saveImageToGallery(context, file)
            if (savedUri != null) {
                capturedImageUri = savedUri
            }
            Toast.makeText(context, "Image saved to gallery!", Toast.LENGTH_SHORT).show()

            // Navigate back to PhotoGalleryScreen
            navController.navigate("gallery") {
                popUpTo("appContent") { inclusive = true } // Remove AppContent from the back stack
            }
        } else {
            Toast.makeText(context, "Failed to capture image", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher for requesting camera permission
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Check and request permission
    LaunchedEffect(Unit) {
        val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            cameraLauncher.launch(uri)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}

/**
 * Save image to gallery using MediaStore.
 */
fun saveImageToGallery(context: Context, file: File): Uri? {
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

    uri?.let {
        resolver.openOutputStream(it).use { outputStream ->
            file.inputStream().copyTo(outputStream!!)
        }
    }

    return uri
}



