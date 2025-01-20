package com.hcmus.ui.display.editimage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController

import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.hcmus.R
import ja.burhanrashid52.photoeditor.OnSaveBitmap

import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import ja.burhanrashid52.photoeditor.SaveFileResult
import ja.burhanrashid52.photoeditor.SaveSettings
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EditImageScreen(
    photoUri: String,
    navController: NavController
) {
    val context = LocalContext.current as Activity
    val originalBitmap = remember { mutableStateOf<Bitmap?>(null) }
    val editedBitmap = remember { mutableStateOf<Bitmap?>(null) }

    val lifecycleOwner = LocalLifecycleOwner.current
    val photoEditorView = remember { mutableStateOf<PhotoEditorView?>(null) }
    val photoEditor = remember { mutableStateOf<PhotoEditor?>(null) }

    var isFilterMenuVisible by remember { mutableStateOf(false) }
    var selectedBrushColor by remember { mutableStateOf(Color.Red) }
    var selectedTextColor by remember { mutableStateOf(Color.Blue) }
    var isBrushActive by remember { mutableStateOf(false) }
    var brushSize by remember { mutableStateOf(10f) }
    var brushOpacity by remember { mutableStateOf(100) }
    var isTextInputVisible by remember { mutableStateOf(false) }
    var textInput by remember { mutableStateOf("Sample Text") }

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val cropImageLauncher =
        rememberLauncherForActivityResult(CropImageContract()) { result ->
            Log.d("CropImage", "Crop result: ${result.isSuccessful}")
            if (result.isSuccessful) {
                result.uriContent?.let { uri ->
                    Log.d("CropImage", "Crop successful, new URI: $uri")

                    // Cập nhật editedBitmap và hiển thị lên PhotoEditorView
                    val croppedBitmap = loadBitmapFromUri(context, uri)
                    editedBitmap.value = croppedBitmap
                    photoEditorView.value?.source?.setImageBitmap(croppedBitmap)
                }
            } else {
                Log.e("CropImage", "Crop failed: ${result.error}")
            }
        }

    // Load the initial image from URI
    if (originalBitmap.value == null) {
        val uri = Uri.parse(photoUri)
        originalBitmap.value = loadBitmapFromUri(context, uri)
        editedBitmap.value = originalBitmap.value
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(0.dp)
                .fillMaxWidth()
        ) {
            AndroidView(
                factory = { ctx ->
                    val editorView = PhotoEditorView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                    photoEditorView.value = editorView
                    editorView
                },
                modifier = Modifier.fillMaxSize()
            ) { editorView ->
                if (photoEditor.value == null) {
                    photoEditor.value = PhotoEditor.Builder(context, editorView)
                        .setPinchTextScalable(true)
                        .build()

                    bitmap = editedBitmap.value
                    bitmap?.let { editorView.source.setImageBitmap(it) }
                }
            }
        }

        // Main UI
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {


            // Undo and Redo Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,

                ) {
                Button(
                    onClick = { photoEditor.value?.undo() },
                    modifier = Modifier.background(Color.Transparent, shape = CircleShape),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Icon(Icons.Filled.Undo, contentDescription = "Undo", tint = Color.Blue)
                }
                Button(
                    onClick = { photoEditor.value?.redo() },
                    modifier = Modifier.background(Color.Transparent, shape = CircleShape),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Icon(Icons.Filled.Redo, contentDescription = "Redo", tint = Color.Blue)
                }
            }

            // Brush Settings
            if (isBrushActive) {
                BrushSettings(
                    photoEditor = photoEditor.value,
                    onBrushSizeChange = { size -> brushSize = size },
                    brushOpacity = brushOpacity,
                    onBrushOpacityChange = { opacity ->
                        brushOpacity = opacity
                        photoEditor.value?.setOpacity(opacity)
                    },
                    selectedBrushColor = selectedBrushColor,
                    onBrushColorChange = { color ->
                        selectedBrushColor = color
                        photoEditor.value?.brushColor = color.toArgb()
                    }
                )
            }

            // Filter Menu
            if (isFilterMenuVisible) {
                FilterMenu(
                    photoEditor = photoEditor.value,
                    onClose = { isFilterMenuVisible = false }
                )
            }
            Spacer(modifier = Modifier.height(2.dp))

            // Button Bar for Brush, Text, Eraser, Filter, and Crop
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    listOf(
                        "Brush",
                        "Text",
                        "Eraser",
                        "Filter",
                        "Crop",
                    )
                ) { action ->
                    Button(
                        onClick = {
                            when (action) {
                                "Brush" -> {
                                    isBrushActive = true
                                    photoEditor.value?.setBrushDrawingMode(true)
                                }

                                "Text" -> {
                                    isBrushActive = false
                                    isTextInputVisible = true
                                }

                                "Eraser" -> {
                                    isBrushActive = false
                                    photoEditor.value?.brushEraser()
                                }

                                "Filter" -> {
                                    isBrushActive = false
                                    isFilterMenuVisible = !isFilterMenuVisible
                                }

                                "Crop" -> {
                                    val uri = saveBitmapToTempFile(context, editedBitmap.value!!)
                                    val cropOptions = CropImageContractOptions(uri, CropImageOptions())
                                    cropImageLauncher.launch(cropOptions)
                                }

                            }
                        },
                        modifier = Modifier
                            .background(Color.Transparent, shape = CircleShape)
                            .padding(5.dp)
                    ) {
                        when (action) {
                            "Brush" -> Icon(
                                painter = painterResource(id = R.drawable.brush_icon),
                                contentDescription = "Brush",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)

                            )

                            "Text" -> Icon(
                                painter = painterResource(id = R.drawable.text_icon),
                                contentDescription = "Text",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )

                            "Eraser" -> Icon(
                                painter = painterResource(id = R.drawable.eraser_icon),
                                contentDescription = "Eraser",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )

                            "Filter" -> Icon(
                                painter = painterResource(id = R.drawable.filter_icon),
                                contentDescription = "Filter",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )

                            "Crop" -> Icon(
                                painter = painterResource(id = R.drawable.crop_icon),
                                contentDescription = "Crop",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )


                        }
                    }
                }
            }

            // Bottom Button Bar for Save and Cancel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.width(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue,
                        contentColor = Color.White
                    )
                ) {
                    Text("Cancel", color = Color.White)
                }
                Button(
                    onClick = {
                        lifecycleOwner.lifecycleScope.launch {
                            saveImageAsBitmap(context, photoEditor.value) {
                                navController.popBackStack() // Successfully saved, go back
                            }
                        }
                    },
                    modifier = Modifier.width(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue,
                        contentColor = Color.White
                    )
                ) {
                    Text("Save", color = Color.White)
                }
            }


            // Text Input Dialog
            if (isTextInputVisible) {
                AlertDialog(
                    onDismissRequest = { isTextInputVisible = false },
                    title = { Text("Add Text") },
                    text = {
                        Column {
                            TextField(
                                value = textInput,
                                onValueChange = { textInput = it },
                                label = { Text("Enter Text") }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            ColorPickerRow(
                                colors = listOf(Color.Red, Color.Blue, Color.Yellow),
                                selectedColor = selectedTextColor,
                                onColorSelected = { color -> selectedTextColor = color }
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                photoEditor.value?.addText(textInput, selectedTextColor.toArgb())
                                isTextInputVisible = false
                            }
                        ) {
                            Text("Add")
                        }
                    }
                )
            }
        }
    }
}

// Bitmap Loading Helper
fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        inputStream?.use { BitmapFactory.decodeStream(it) }
    } catch (e: Exception) {
        Log.e("LoadBitmap", "Error loading bitmap from URI", e)
        null
    }
}

private fun saveImageAsBitmap(context: Context, photoEditor: PhotoEditor?, onSaved: () -> Unit) {
    photoEditor?.saveAsBitmap(object : OnSaveBitmap {
        override fun onBitmapReady(saveBitmap: Bitmap) {
            saveBitmap?.let { bitmap ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "edited_image_${System.currentTimeMillis()}.png")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/EditedImages")
                }

                val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let {
                    try {
                        context.contentResolver.openOutputStream(it)?.use { outputStream ->
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                            onSaved()
                        }
                    } catch (e: Exception) {
                        Log.e("SaveImage", "Error saving image", e)
                    }
                }
            }
        }

        fun onFailure(e: Exception?) {
            Log.e("SaveImage", "Failed to save bitmap", e)
        }
    })
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
