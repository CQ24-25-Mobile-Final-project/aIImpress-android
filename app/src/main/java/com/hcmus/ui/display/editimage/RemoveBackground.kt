package com.hcmus.ui.display.editimage


import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.Segmenter
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.nio.ByteBuffer

fun mergeBitmaps(bmp1: Bitmap, bmp2: Bitmap): Bitmap? {
    val merged = bmp1.config?.let { Bitmap.createBitmap(bmp1.width, bmp1.height, it) }
    val canvas = merged?.let { Canvas(it) }
    if (canvas != null) {
        canvas.drawBitmap(bmp1, Matrix(), null)
    }
    if (canvas != null) {
        canvas.drawBitmap(bmp2, Matrix(), null)
    }
    return merged
}


fun resizeBitmap(bmp: Bitmap, width: Int, height: Int): Bitmap {
    return Bitmap.createScaledBitmap(bmp, width, height, false)
}
class SegmentHelper(private val listener: ProcessedListener) {
    private val segmenter: Segmenter
    private lateinit var maskBuffer: ByteBuffer
    private var maskWidth = 0
    private var maskHeight = 0

    init {
        val options = SelfieSegmenterOptions.Builder()
            .setDetectorMode(SelfieSegmenterOptions.SINGLE_IMAGE_MODE)
            .build()

        segmenter = Segmentation.getClient(options)
    }

    fun processImage(image: Bitmap) {
        val input = InputImage.fromBitmap(image, 0)
        segmenter.process(input)
            .addOnSuccessListener { segmentationMask ->
                maskBuffer = segmentationMask.buffer
                maskWidth = segmentationMask.width
                maskHeight = segmentationMask.height
                listener.imageProcessed()
            }
            .addOnFailureListener { e ->
                Log.e("SegmentHelper", "Image processing failed: $e")
            }
    }

    fun generateMaskImage(image: Bitmap): Bitmap {
        val maskBitmap = Bitmap.createBitmap(maskWidth, maskHeight, Bitmap.Config.ARGB_8888)



        val tempBitmap = if (image.config == Bitmap.Config.HARDWARE) {
            image.copy(Bitmap.Config.ARGB_8888, true)
        } else {
            image
        }

        for (y in 0 until maskHeight) {
            for (x in 0 until maskWidth) {
                if (x < tempBitmap.width && y < tempBitmap.height) {
                    val confidence = maskBuffer.float
                    val alpha = (confidence * 255).toInt()
                    val pixelColor = if (alpha > 0) tempBitmap.getPixel(x, y) else Color.TRANSPARENT
                    maskBitmap.setPixel(x, y, Color.argb(alpha, Color.red(pixelColor), Color.green(pixelColor), Color.blue(pixelColor)))
                }
            }
        }
        maskBuffer.rewind()

        return maskBitmap
    }
    fun generateMaskBgImage(image: Bitmap, bg: Bitmap): Bitmap? {
        val bgBitmap = bg.copy(Bitmap.Config.ARGB_8888, true)

        for (y in 0 until maskHeight) {
            for (x in 0 until maskWidth) {
                if (x < bg.width && y < bg.height) {
                    val bgConfidence = ((1.0 - maskBuffer.float) * 2).toInt()
                    var bgPixel = bg.getPixel(x, y)
                    bgPixel = ColorUtils.setAlphaComponent(bgPixel, bgConfidence)
                    bgBitmap.setPixel(x, y, bgPixel)
                }
            }
        }
        maskBuffer.rewind()

        return mergeBitmaps(image, bgBitmap)
    }


}

interface ProcessedListener {
    fun imageProcessed()
}
class MainViewModel : ViewModel(), ProcessedListener {
    private val _currentImage = MutableLiveData<Bitmap>()
    val currentImage: LiveData<Bitmap> = _currentImage

    private val _selectedMode = MutableLiveData<DisplayMode>()
    val selectedMode: LiveData<DisplayMode> = _selectedMode

    var choseFront = true
    var isInitialized = false

    private var _foregroundImage: Bitmap? = null
    private var _maskImage: Bitmap? = null
    private var _maskBgImage: Bitmap? = null
    private var _bgImage: Bitmap? = null

    private var _segmentHelper: SegmentHelper = SegmentHelper(this)

    init {
        // At startup, normal mode is selected
        _selectedMode.value = DisplayMode.NORMAL
    }

    fun imageChosen(bmp: Bitmap) {
        if (choseFront) {
            _foregroundImage = bmp
            _bgImage = _foregroundImage?.let { _bgImage?.let { bg -> resizeBitmap(bg, it.width, it.height) } }
            _foregroundImage?.let { _segmentHelper.processImage(it) }
        } else {
            _bgImage = bmp
            _maskBgImage = _foregroundImage?.let { fg -> _bgImage?.let { bg -> _segmentHelper.generateMaskBgImage(fg, bg) } }
            setCurrentImage()
        }
    }

    private fun setCurrentImage() {
        _currentImage.value = when (_selectedMode.value) {
            DisplayMode.NORMAL -> {
                Log.d("MainViewModel", "Displaying normal image")
                _foregroundImage
            }
            DisplayMode.MASK -> {
                Log.d("MainViewModel", "Displaying mask image")
                _maskImage
            }
            DisplayMode.CUSTOM_BG -> {
                Log.d("MainViewModel", "Displaying image with custom background")
                _maskBgImage
            }
            else -> {
                Log.e("MainViewModel", "Invalid mode selected: ${_selectedMode.value}")
                _foregroundImage
            }
        }
    }

    fun modeSelected(mode: DisplayMode) {
        _selectedMode.value = mode
        setCurrentImage()
    }


    override fun imageProcessed() {
        _foregroundImage?.let {
            _maskImage = _segmentHelper.generateMaskImage(it)
            _maskBgImage = _bgImage?.let { bg -> _segmentHelper.generateMaskBgImage(it, bg) }
            setCurrentImage()
        }
    }
}
val appModule = module {
    viewModel { MainViewModel() }
}
enum class DisplayMode {
    NORMAL,
    MASK,
    CUSTOM_BG
}
@Composable
fun ImageSegmenter(navController: NavController, viewModel: MainViewModel = koinViewModel()) {
    val context = LocalContext.current

    // Observe LiveData as State in Compose
    val currentImage by viewModel.currentImage.observeAsState()
    val selectedMode by viewModel.selectedMode.observeAsState()

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        uri?.let {
            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            }
            viewModel.imageChosen(bitmap)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = { galleryLauncher.launch("image/*") }) {
            Text("Select Image from Gallery")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(
                onClick = { viewModel.modeSelected(DisplayMode.NORMAL) },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Normal Mode")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { viewModel.modeSelected(DisplayMode.MASK) },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Mask Mode")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        currentImage?.let { image ->
            Text("Current Image in Mode: $selectedMode")
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                bitmap = image.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}