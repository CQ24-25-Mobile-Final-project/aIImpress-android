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

import StoryUI
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hcmus.ui.album.AddNewAlbum
import com.hcmus.ui.album.DisplayPhotoInAlbum
import com.hcmus.ui.album.ImagePickerScreen
import com.hcmus.ui.album.MyAlbumScreen
import com.hcmus.ui.album.SelectImageForAlbum
import com.hcmus.ui.display.AppContent
import com.hcmus.ui.display.EditImageScreen
import com.hcmus.ui.display.ImageDetailScreen
import com.hcmus.ui.display.MediaReader
import com.hcmus.ui.display.Photo
import com.hcmus.ui.display.PhotoGalleryScreen
import com.hcmus.ui.display.categorizePhotos
import com.hcmus.ui.secret.AuthenticationScreen
import com.hcmus.ui.secret.SecretPhotoViewScreen
import com.hcmus.ui.story.SharedGalleryScreen

import kotlinx.coroutines.flow.StateFlow

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PhotoGalleryViewModel : ViewModel() {
    private val _categorizedPhotos = MutableStateFlow<Map<String, List<Photo>>>(emptyMap())
    val categorizedPhotos: StateFlow<Map<String, List<Photo>>> = _categorizedPhotos
    // Assume MediaReader is available in your context
    fun fetchCategorizedPhotos(context: Context) {
        val mediaReader = MediaReader(context)
        val photosByDate = mediaReader.getAllMediaFiles()
        val categorized = categorizePhotos(photosByDate)
        _categorizedPhotos.value = categorized
    }
}


@Composable
fun MainNavigation(navController: NavHostController) {
    val context = LocalContext.current
    val photoGalleryViewModel: PhotoGalleryViewModel = hiltViewModel()

    // Load categorized photos when the navigation starts
    LaunchedEffect(Unit) {
        photoGalleryViewModel.fetchCategorizedPhotos(context)
    }

    val categorizedPhotos by photoGalleryViewModel.categorizedPhotos.collectAsState()
    NavHost(navController = navController, startDestination = "gallery") {
        composable("gallery") { PhotoGalleryScreen(navController = navController) }

        composable("authentication") { AuthenticationScreen(navController = navController) }

        composable("view") {
            SecretPhotoViewScreen(
                onBackPressed = {
                    navController.navigate("main") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }

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

        composable("MyAlbumScreen") {
            MyAlbumScreen(navController = navController)
        }
        composable("AddNewAlbum") {
            AddNewAlbum(navController = navController)
        }

        composable("SelectImageForAlbum") {
            SelectImageForAlbum(navController = navController)
        }

        composable("DisplayPhotoInAlbum") {
            DisplayPhotoInAlbum(navController = navController)
        }

        composable("imagePicker") {
            ImagePickerScreen(context = LocalContext.current)
        }

        composable("shareScreen") { SharedGalleryScreen(navController = navController) }

        composable("galleryScreen") { PhotoGalleryScreen(navController = navController) }

        composable("appContent") { AppContent(navController = navController) }
        composable(
            route = "storyUI/{category}",
            arguments = listOf(navArgument("category") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""

            val photosForCategory = categorizedPhotos[category]

            if (photosForCategory != null) {
                StoryUI(
                    navController = navController,
                    startIndex = 0,
                    photos = photosForCategory
                )
            }
        }

//
    }
}
