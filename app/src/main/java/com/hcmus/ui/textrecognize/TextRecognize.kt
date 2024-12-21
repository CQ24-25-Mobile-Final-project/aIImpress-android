package com.hcmus.ui.textrecognize

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import androidx.compose.ui.Modifier
import android.net.Uri
import androidx.compose.ui.text.style.TextOverflow
import com.google.mlkit.vision.text.latin.TextRecognizerOptions


@Composable
fun TextRecognitionResultBar(showResult: Boolean, recognizedText: String) {
    if (showResult) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray.copy(alpha = 0.5f))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (recognizedText.isNotEmpty()) recognizedText else "No text found",
                fontSize = 14.sp,
                color = Color.White,
            )
        }
    }
}
fun recognizeText(context: Context, photoUri: String, onResult: (String) -> Unit) {
    val image: InputImage
    try {
        val uri = Uri.parse(photoUri)
        image = InputImage.fromFilePath(context, uri)

        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                onResult(visionText.text)
            }
            .addOnFailureListener { e ->
                onResult("Failed to recognize text: ${e.message}")
            }
    } catch (e: Exception) {
        onResult("Error loading image: ${e.message}")
    }
}