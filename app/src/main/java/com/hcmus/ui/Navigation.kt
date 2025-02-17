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

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.hcmus.domain.Screen
import com.hcmus.presentation.AiGenerateImageViewModel
import com.hcmus.presentation.screens.GenerateImageScreen
import com.hcmus.presentation.screens.ImageScreen
import com.hcmus.presentation.screens.LoadingScreen
import com.hcmus.ui.album.AddNewAlbum
import com.hcmus.ui.album.DisplayPhotoInAlbum
import com.hcmus.ui.album.ImagePickerScreen
import com.hcmus.ui.album.MyAlbumScreen
import com.hcmus.ui.album.SelectImageForAlbum
import com.hcmus.ui.display.AppContent
import com.hcmus.ui.display.ImageDetailScreen
import com.hcmus.ui.display.MediaReader
import com.hcmus.ui.display.Photo
import com.hcmus.ui.display.PhotoGalleryScreen
import com.hcmus.ui.display.categorizePhotos
import com.hcmus.ui.secret.AuthenticationScreen
import com.hcmus.ui.secret.SecretPhotoViewScreen
import com.hcmus.ui.story.SharedGalleryScreen
import com.hcmus.auth.AuthResponse
import com.hcmus.auth.AuthenticationManager
import com.hcmus.auth.JwtManager
import com.hcmus.data.CloudFirestoreService
import com.hcmus.data.ContextStore
import com.hcmus.data.model.Credential
import com.hcmus.data.model.CredentialDatabase
import com.hcmus.data.model.User
import com.hcmus.ui.Trash.TrashAlbumScreen
import com.hcmus.ui.display.ImageDescriptionScreen
import com.hcmus.ui.display.MapPhotoView


import com.hcmus.ui.display.getAllPhotoPaths
import com.hcmus.ui.screens.LoginScreen
import com.hcmus.ui.screens.SignInScreen
import com.hcmus.ui.secret.AlbumModel
import com.hcmus.ui.secret.CreatePinScreen
import com.hcmus.ui.secret.DisplayPhotoInAlbumScreen
import com.hcmus.ui.secret.SelectAlbumToAddPhoto
import com.hcmus.ui.secret.SelectPhotoForAlbum
import com.hcmus.ui.display.editimage.EditImageScreen
import com.hcmus.ui.display.editimage.ImageSegmenter

import com.hcmus.ui.edituser.PrivacyAndPolicyScreen
import com.hcmus.ui.edituser.EditProfileScreen
import com.hcmus.ui.edituser.ProfileScreen
import com.hcmus.ui.story.StoryUI
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


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
fun Navigation(viewModel: AiGenerateImageViewModel, navController: NavHostController) {
    val context = LocalContext.current
    val photoGalleryViewModel: PhotoGalleryViewModel = hiltViewModel()
    val authManager = remember {
        AuthenticationManager(context)
    }
    val coroutineScope = rememberCoroutineScope()
    val cloudFirestore = CloudFirestoreService()
    val credentialRepository = CredentialDatabase.getInstance(context).credentialRepository()
    var isLoggedIn by remember { mutableStateOf(false) }
    val jwtManager = JwtManager()

    // Load categorized photos when the navigation starts
    LaunchedEffect(Unit) {
        val cred = credentialRepository.get()
        if (cred?.token !== null && jwtManager.verify(cred.token)) {
            photoGalleryViewModel.fetchCategorizedPhotos(context)
            ContextStore.set(context, "email", cred.email)
            isLoggedIn = true
        } else {
            credentialRepository.delete()
        }
    }

    val categorizedPhotos by photoGalleryViewModel.categorizedPhotos.collectAsState()
    NavHost(navController = navController, startDestination = if (isLoggedIn) "gallery" else "login") {
        composable("generate") {
            GenerateImageScreen(
                viewModel = viewModel,
                navController = navController
            )
        }

        composable("login") {
            LoginScreen(
                onLoginEmail = { email, password ->
                    authManager.loginWithEmail(email, password).onEach { response ->
                        if (response is AuthResponse.Success) {
                            Log.d("Login", "Success: $response")
                            Toast.makeText(context, "Login Success", Toast.LENGTH_SHORT).show()

                            // Điều hướng đến màn hình Gallery và xóa Login khỏi stack
                            navController.navigate("gallery") {
                                popUpTo("login") { inclusive = true }
                            }
                            isLoggedIn = true

                            // Lưu email người dùng vào ContextStore
                            ContextStore.set(context, "email", email)

                            // save to db
                            val creds = Credential()
                            creds.email = email
                            creds.token = jwtManager.sign()
                            credentialRepository.insert(creds)
                        } else {
                            Log.d("Login", "Error: $response")
                            Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                        }
                    }.launchIn(coroutineScope)
                },
                onSignIn = {
                    // Điều hướng đến màn hình SignIn
                    navController.navigate("signIn") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onLoginGoogle = { user ->
                    if (user != null) {
                        Log.d("Login", "Google Login Success: ${user.email}")
                        Toast.makeText(context, "Login Success", Toast.LENGTH_SHORT).show()

                        // Điều hướng đến màn hình Gallery
                        navController.navigate("gallery") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        Log.d("Login", "Google Login Failed")
                        Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        composable(Screen.HomeScreen.route) {
            GenerateImageScreen(viewModel, navController)
        }
        composable(Screen.LoadingScreen.route) {
            LoadingScreen(viewModel, navController)
        }
        composable(Screen.ImageScreen.route) {
            ImageScreen(viewModel, navController)
        }
        composable("signIn") {
            SignInScreen(
                onSignIn = { email, password ->
                    Log.d("Login", "Attempting to create account with email: $email")

                    authManager.createAccountWithEmail(email, password)
                        .onEach { response ->
                            when (response) {
                                is AuthResponse.Success -> {
                                    Log.d("Login", "Account creation successful")
                                    Toast.makeText(
                                        context,
                                        "Account created successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    cloudFirestore.db
                                        .document(email)
                                        .set(User(email = email))

                                    navController.navigate("login")
                                }

                                is AuthResponse.Error -> {
                                    Log.e("Login", "Account creation failed: ${response.message}")
                                    Toast.makeText(
                                        context,
                                        "Failed: ${response.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                        .launchIn(coroutineScope)
                }
            )
        }
        composable("photo_map") {
            val context = LocalContext.current
            val photoUris = getAllPhotoPaths(context)
            MapPhotoView(photos = photoUris, navController) // Assuming URIs are passed as photo data
        }
        composable("imageDescription/{photoUri}") { backStackEntry ->
            val photoUri = backStackEntry.arguments?.getString("photoUri") ?: ""
            ImageDescriptionScreen(
                photoUri = photoUri,
                viewModel = viewModel(),
                navController = navController
            )
        }

        composable("remove_background_screen") {
            ImageSegmenter(navController = navController)
        }
        composable("gallery") { PhotoGalleryScreen(navController = navController) }
        composable("editUser") { ProfileScreen(navController = navController) }
        composable("editProfile") { EditProfileScreen(navController = navController) }
        composable("authentication") { AuthenticationScreen(navController = navController) }
        composable("privacy_policy") { PrivacyAndPolicyScreen(navController = navController) }
        composable("view") {
            SecretPhotoViewScreen(
                navController = navController,
                onBackPressed = {
                    navController.navigate("") {
                        popUpTo("gallery") { inclusive = true }
                    }
                },
                context = context
            )
        }
        composable("display_photo_in_album/{albumName}") { backStackEntry ->
            val albumName = backStackEntry.arguments?.getString("albumName")
            if (albumName != null) {
                DisplayPhotoInAlbumScreen(
                    navController = navController,
                    albumName = albumName,
                    context = context
                )
            }
        }

        composable("imagePicker") {
            ImagePickerScreen(context = LocalContext.current)
        }
        composable("select_photo_for_album") {
            val albumModel: AlbumModel =
                hiltViewModel() // Gọi hiltViewModel() bên trong hàm @Composable
            SelectPhotoForAlbum(
                navController = navController,
                albumModel = albumModel,
                context = context
            )
        }
        composable("select_album_to_add_photo") {
            val albumModel: AlbumModel =
                hiltViewModel() // Gọi hiltViewModel() bên trong hàm @Composable

            SelectAlbumToAddPhoto(
                navController = navController,
                albumModel = albumModel,
                context = context
            )
        }


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

        composable("trash_album_screen") {
            TrashAlbumScreen(navController = navController)
        }

        composable("create_pin") { CreatePinScreen(navController) }
    }
}
