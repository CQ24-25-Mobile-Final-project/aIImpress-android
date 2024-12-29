package com.hcmus.ui.display.editimage

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import ja.burhanrashid52.photoeditor.SaveFileResult
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun BrushSettings(
    photoEditor: PhotoEditor?,
    onBrushSizeChange: (Float) -> Unit,
    brushOpacity: Int,
    onBrushOpacityChange: (Int) -> Unit,
    selectedBrushColor: Color,
    onBrushColorChange: (Color) -> Unit
) {
    Text("Brush Settings", style = MaterialTheme.typography.titleMedium)

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Brush Size Slider


        // Brush Opacity Slider
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Opacity: ${brushOpacity}%")
            Slider(
                value = brushOpacity.toFloat(),
                onValueChange = { onBrushOpacityChange(it.toInt()) },
                valueRange = 10f..100f,
                modifier = Modifier.width(150.dp)
            )
        }

        // Brush Color Picker
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Color")
            ColorPickerRow(
                colors = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Cyan, Color.Magenta),
                selectedColor = selectedBrushColor,
                onColorSelected = onBrushColorChange
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
}
