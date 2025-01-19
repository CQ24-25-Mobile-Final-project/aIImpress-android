package com.hcmus.ui.display


import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.hcmus.ui.theme.BluePrimary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.SocketTimeoutException
@Composable
fun ImageDescriptionScreen(photoUri: String, viewModel: MainViewModel, navController: NavController) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val description by viewModel.caption.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hiển thị ảnh
        Image(
            painter = rememberAsyncImagePainter(model = Uri.parse(photoUri)),
            contentDescription = "Photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)

        )

        Spacer(modifier = Modifier.height(16.dp))

        // Hiển thị mô tả ảnh
        when {
            description.isNotEmpty() -> {
                Text(
                    text = description,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
            error.isNotEmpty() -> {
                Text(
                    text = error,
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
            }
            else -> {
                Text(
                    text = "Generating description...",
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary,
                    contentColor = Color.White
                ),
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Back")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    clipboardManager.setText(AnnotatedString(description))
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary,
                    contentColor = Color.White
                ),
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Copy Text")
            }
        }


    }

    // Gửi yêu cầu lấy mô tả ảnh nếu chưa có
    if (description.isEmpty() && error.isEmpty()) {
        val bitmap = loadBitmapFromUri(context, photoUri) // Hàm để load bitmap từ Uri
        bitmap?.let { viewModel.onTakePhoto(it) }
    }
}

// Hàm để load Bitmap từ Uri
fun loadBitmapFromUri(context: Context, photoUri: String): Bitmap? {
    val uri = Uri.parse(photoUri)
    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
    } catch (e: Exception) {
        Log.e("ImageDescription", "Failed to load bitmap: ${e.message}", e)
        null
    }
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _caption = MutableStateFlow<String>("")
    val caption = _caption.asStateFlow()

    private val _error = MutableStateFlow<String>("")
    val error = _error.asStateFlow()

    fun onTakePhoto(bitmap: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        val client = OkHttpClient()
        val requestBody: RequestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray)
        val request: Request = Request.Builder()
            .url("https://api-inference.huggingface.co/models/nlpconnect/vit-gpt2-image-captioning")
            .addHeader("Authorization", "Bearer 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")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                viewModelScope.launch(Dispatchers.Main) {
                    _error.value = if (e is SocketTimeoutException) {
                        "Timeout error. Please wait...!"
                    } else {
                        "Failed to generate description. Try again."
                    }
                }
                Log.e("ImageDescription", "Error during API call: ${e.message}", e)
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    val responseString = response.body?.string()
                    try {
                        val jsonResponseArray = JSONArray(responseString)
                        val firstObject = jsonResponseArray.getJSONObject(0)
                        val caption = firstObject.getString("generated_text")
                        viewModelScope.launch(Dispatchers.Main) {
                            _caption.value = caption
                        }
                    } catch (e: Exception) {
                        viewModelScope.launch(Dispatchers.Main) {
                            _error.value = "Error parsing response."
                        }
                        Log.e("ImageDescription", "Error parsing JSON: ${e.message}", e)
                    }
                } else {
                    viewModelScope.launch(Dispatchers.Main) {
                        _error.value = "API call failed with status: ${response.code}"
                    }
                }
            }
        })
    }
}
