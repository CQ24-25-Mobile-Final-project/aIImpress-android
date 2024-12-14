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

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
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
import com.hcmus.ui.display.PhotoGalleryScreen
import com.hcmus.ui.secret.AuthenticationScreen
import com.hcmus.ui.secret.SecretPhotoViewScreen
import com.hcmus.ui.story.SharedGalleryScreen
@Composable
fun MainNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "gallery") {
        composable("gallery") { PhotoGalleryScreen(navController) }
        composable("authentication") { AuthenticationScreen(navController) }
        composable("view") { SecretPhotoViewScreen(
            onBackPressed = {
                navController.navigate("main") {
                    popUpTo("main") { inclusive = true }// dùng để xóa các màn hình trung gian
                }
            }
        ) }
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
            MyAlbumScreen(navController = navController) // Truyền NavController
        }
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
            ImagePickerScreen(context = LocalContext.current)
        }
        composable("shareScreen") { SharedGalleryScreen(navController) }
        composable("galleryScreen") { PhotoGalleryScreen(navController) }
        composable("appContent") { AppContent(navController) }
    }
}
