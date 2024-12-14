/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hcmus.ui

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.google.accompanist.insets.LocalWindowInsets
import com.hcmus.ui.album.AddNewAlbum
import com.hcmus.ui.album.DisplayPhotoInAlbum
import com.hcmus.ui.album.ImagePickerScreen
import com.hcmus.ui.album.MyAlbumScreen
import com.hcmus.ui.album.SelectImageForAlbum
import com.hcmus.ui.components.CustomBottomBar
import com.hcmus.ui.components.GalleryTopBar
import com.hcmus.ui.display.AppContent
import com.hcmus.ui.display.EditImageScreen
import com.hcmus.ui.display.ImageDetailScreen
import com.hcmus.ui.display.PhotoGalleryScreen
import com.hcmus.ui.story.SharedGalleryScreen
import dagger.hilt.android.AndroidEntryPoint
import com.hcmus.ui.theme.MyApplicationTheme

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
