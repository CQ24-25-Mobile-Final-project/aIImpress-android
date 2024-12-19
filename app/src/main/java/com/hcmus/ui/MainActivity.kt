package com.hcmus.ui

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import com.hcmus.ui.theme.MyApplicationTheme
import androidx.core.app.ActivityCompat

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge() // Enable edge-to-edge fullscreen layout

    // Request permissions dynamically based on Android version
    val permissions = if (Build.VERSION.SDK_INT >= 33) {
      arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
      arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    ActivityCompat.requestPermissions(this, permissions, 0)

    setContent {
      MyApplicationTheme(dynamicColor = false) {
        val navController = rememberNavController()

        Scaffold(
          //modifier = Modifier.fillMaxSize(),
          content = { paddingValues ->
            SafeArea {
              Surface(modifier = Modifier.fillMaxSize()) {
                MainNavigation(navController = navController)
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
  // Use LocalDensity to convert from pixels to dp
  val insets = with(LocalDensity.current) {
    val systemGestureInsets = LocalView.current.rootWindowInsets?.systemGestureInsets
    Insets(
      bottom = systemGestureInsets?.bottom?.toDp() ?: 0.dp,
      left = systemGestureInsets?.left?.toDp() ?: 0.dp,
      right = systemGestureInsets?.right?.toDp() ?: 0.dp
    )
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(
        start = insets.left,
        end = insets.right,
        bottom = insets.bottom
      )
  ) {
    content()
  }
}

data class Insets(
  val bottom: Dp,
  val left: Dp,
  val right: Dp
)
