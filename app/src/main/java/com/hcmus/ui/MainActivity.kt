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

import android.content.res.Resources.Theme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hcmus.ui.album.AddNewAlbum
import com.hcmus.ui.album.DisplayPhotoInAlbum
import com.hcmus.ui.album.ImagePickerScreen
import com.hcmus.ui.album.MyAlbumScreen
import com.hcmus.ui.album.SelectImageForAlbum
import dagger.hilt.android.AndroidEntryPoint
import com.hcmus.ui.theme.MyApplicationTheme
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(dynamicColor = false) {
                var navController = rememberNavController()
                Scaffold(
                    topBar = {

                    }
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it),
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = "MyAlbumScreen" // Màn hình bắt đầu
                        ) {
                            composable("MyAlbumScreen") {
                                MyAlbumScreen(navController = navController) // Truyền NavController
                            }
                            // AddNewAlbum screen
                            composable("AddNewAlbum") {
                                AddNewAlbum(navController = navController) // Truyền NavController
                            }

                            composable("SelectImageForAlbum") {
                                SelectImageForAlbum(navController = navController)
                            }

                            composable("DisplayPhotoInAlbum") {
                                DisplayPhotoInAlbum(navController = navController)
                            }

                            composable("imagePicker") {
                                val context = LocalContext.current
                                ImagePickerScreen(context = context)
                            }
                        }
                    }
                }
            }
        }
    }
}