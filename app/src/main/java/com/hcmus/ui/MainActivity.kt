package com.hcmus.ui

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
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
import com.hcmus.ui.display.editimage.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    startKoin {
      androidContext(this@MainActivity)
      modules(appModule)  // Add your Koin modules here
    }
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
  val insets = WindowInsets.navigationBars.asPaddingValues()
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(insets)
  ) {
    content()
  }
}

