package com.hcmus.ui.display

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.core.net.toUri
import com.canhub.cropper.CropImage.CancelledResult.bitmap
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.hcmus.ui.components.fontFamily


import java.io.File
import java.io.FileOutputStream


@Composable
fun EditImageScreen(
    photoUri: String,
    navController: NavController
) {
    val context = LocalContext.current as Activity
    val originalBitmap = remember { mutableStateOf<Bitmap?>(null) }
    val editedBitmap = remember { mutableStateOf<Bitmap?>(null) }

    val textPosition = remember { mutableStateOf(Offset(50f, 50f)) }
    val isDragging = remember { mutableStateOf(false) }
    val isBlurred = remember { mutableStateOf(false) }
    val filterType = remember { mutableStateOf(FilterType.NONE) }
    val decodedUri = Uri.decode(photoUri)
    // Cropper launcher
    val cropImageLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            result.uriContent?.let { uri ->
                editedBitmap.value = loadBitmapFromUri(context, uri)

            }
        }
    }


    // Load the initial image
    if (originalBitmap.value == null) {
        originalBitmap.value = loadBitmapFromUri(context, Uri.parse(photoUri))
        editedBitmap.value = originalBitmap.value
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Image Preview
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            editedBitmap.value?.let { bitmap ->
                val colorFilter = when (filterType.value) {
                    FilterType.TINT_GREEN_DARKEN -> ColorFilter.tint(Color.Green, blendMode = BlendMode.Darken)
                    FilterType.BLACK_WHITE -> ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                    FilterType.CONTRAST_BRIGHTNESS -> {
                        val contrast = 2f
                        val brightness = -180f
                        ColorFilter.colorMatrix(ColorMatrix(floatArrayOf(
                            contrast, 0f, 0f, 0f, brightness,
                            0f, contrast, 0f, 0f, brightness,
                            0f, 0f, contrast, 0f, brightness,
                            0f, 0f, 0f, 1f, 0f
                        )))
                    }
                    FilterType.INVERT -> ColorFilter.colorMatrix(ColorMatrix(floatArrayOf(
                        -1f, 0f, 0f, 0f, 255f,
                        0f, -1f, 0f, 0f, 255f,
                        0f, 0f, -1f, 0f, 255f,
                        0f, 0f, 0f, 1f, 0f
                    )))
                    FilterType.NONE -> null
                }

                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Edited Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .let { mod ->
                            if (isBlurred.value) {
                                mod.blur(
                                    radiusX = 10.dp,
                                    radiusY = 10.dp,
                                    edgeTreatment = BlurredEdgeTreatment(RoundedCornerShape(8.dp))
                                )
                            } else {
                                mod
                            }
                        },
                    colorFilter = colorFilter,
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Actions Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listOf("Filter", "Crop", "Blur", "Add Text")) { action ->
                Button(
                    onClick = {
                        when (action) {
                            "Filter" -> filterType.value = filterType.value.next()
                            "Crop" -> {
                                val uri = saveBitmapToTempFile(context, editedBitmap.value!!)
                                val cropOptions = CropImageContractOptions(uri, CropImageOptions())
                                cropImageLauncher.launch(cropOptions)
                            }
                            "Blur" -> isBlurred.value = !isBlurred.value
                        }
                    },
                    modifier = Modifier.padding(horizontal = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEAF6FE))
                ) {
                    Text(text = action, color = Color(0xFF1B87C9), fontFamily = fontFamily)
                }
            }
        }

        // Save and Cancel Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor =MaterialTheme.colorScheme.secondary)
            ) {
                Text("Cancel", fontFamily = fontFamily, color = MaterialTheme.colorScheme.primary)
            }
            Button(
                onClick = {
                    editedBitmap.value?.let { bitmap ->

                        val savedUri = saveBitmapToFile(context, bitmap)


                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Save copy", color = Color.White, fontFamily = fontFamily)
            }

        }
    }
}

fun saveBitmapToFile(context: Activity, value: Bitmap): Uri? {
    // Create a new file in the app's external files directory (you can change the path if needed)
    val fileName = "edited_image_${System.currentTimeMillis()}.png"
    val file = File(context.getExternalFilesDir(null), fileName)

    FileOutputStream(file).use { out ->
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, out)
        out.flush()
    }

    // Return the URI of the saved file
    return file.toUri()
}

// Helper function to load Bitmap from URI
private fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    } else {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}


// Enum to manage filter states
enum class FilterType {
    NONE,
    TINT_GREEN_DARKEN,
    BLACK_WHITE,
    CONTRAST_BRIGHTNESS,
    INVERT;

    fun next(): FilterType = values()[(ordinal + 1) % values().size]
}

private fun saveBitmapToTempFile(context: Context, bitmap: Bitmap): Uri {
    val tempFile = File.createTempFile("temp_image", ".png", context.cacheDir).apply {
        deleteOnExit()
    }
    FileOutputStream(tempFile).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        out.flush()
    }
    return tempFile.toUri()
}

