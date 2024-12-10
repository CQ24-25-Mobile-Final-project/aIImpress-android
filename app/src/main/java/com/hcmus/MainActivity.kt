package com.hcmus

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hcmus.ui.theme.PhotoappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Kích hoạt giao diện toàn màn hình

        // Request permissions based on Android version
        val permissions = if (Build.VERSION.SDK_INT >= 33) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        ActivityCompat.requestPermissions(this, permissions, 0)

        setContent {

            PhotoappTheme {
                val navController = rememberNavController()
                // Áp dụng Safe Area thông qua WindowInsets
                SafeArea {
                    NavHost(navController, startDestination = "gallery") {
                        composable("gallery") { PhotoGalleryScreen(navController) }
                        composable(
                            route = "imageDetail/{photoUri}",
                            arguments = listOf(navArgument("photoUri") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            val photoUri = backStackEntry.arguments?.getString("photoUri") ?: ""
                            ImageDetailScreen(photoUri = photoUri, navController = navController)
                        }
                        composable(
                            route = "editImage/{photoUri}",
                            arguments = listOf(navArgument("photoUri") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            val photoUri = backStackEntry.arguments?.getString("photoUri") ?: ""
                            EditImageScreen(photoUri = photoUri, navController = navController)
                        }
                        composable("shareScreen") { SharedGalleryScreen(navController) }
                        composable("galleryScreen") { PhotoGalleryScreen(navController) }
                        composable("appContent") { AppContent(navController) }

                    }
                }
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


