package com.hcmus.ui

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.hcmus.presentation.AiGenerateImageViewModel
import com.hcmus.data.appModule
import com.hcmus.ui.theme.MyApplicationTheme
import com.hcmus.ui.theme.AiImageGeneratorTheme
import dagger.hilt.android.AndroidEntryPoint
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.GlobalContext.startKoin

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

  val viewModel: AiGenerateImageViewModel by viewModel()

  @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
  @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Khởi tạo Koin
    startKoin {
      androidContext(this@MainActivity)
      modules(appModule)
    }

    // Yêu cầu quyền truy cập tệp
    val permissions = if (Build.VERSION.SDK_INT >= 33) {
      arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
      arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    ActivityCompat.requestPermissions(this, permissions, 0)

    // Bật chế độ edge-to-edge
    enableEdgeToEdge()

    // Thiết lập giao diện với Jetpack Compose
    setContent {
      AiImageGeneratorTheme {
        val navController = rememberNavController()
        Navigation(viewModel, navController)

      }
      MyApplicationTheme(dynamicColor = false) {
        val navController = rememberNavController()

        Scaffold(
          content = {
            SafeArea {
              Surface(modifier = Modifier.fillMaxSize()) {
                Navigation(viewModel = viewModel, navController = navController)
              }
            }
          }
        )
      }
    }
  }
}

@Composable
fun SafeArea(content: @Composable () -> Unit) {
  val insets = WindowInsets.navigationBars.asPaddingValues()
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(insets)
  ) {
    content()
  }
}
