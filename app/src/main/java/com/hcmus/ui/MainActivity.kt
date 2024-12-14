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
import com.hcmus.ui.screens.LoginScreen
import com.hcmus.ui.secret.AuthenticationScreen
import com.hcmus.ui.secret.SecretPhotoViewScreen
import com.hcmus.ui.story.SharedGalleryScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.hcmus.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import android.view.View
import android.view.ViewTreeObserver

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val content = findViewById<View>(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    content.viewTreeObserver.removeOnPreDrawListener(this)
                    return true
                }
            }
        )
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                MainNavigation(navController = navController)
            }
        }
    }
}

@Composable
fun MainNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("gallery") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }

        // Main App Screens
        composable("gallery") { PhotoGalleryScreen(navController) }
        composable("authentication") { AuthenticationScreen(navController) }
        composable("view") {
            SecretPhotoViewScreen(
                onBackPressed = {
                    navController.navigate("gallery") {
                        popUpTo("gallery") { inclusive = true } // Clear intermediate screens
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

        composable("shareScreen") { SharedGalleryScreen(navController) }
        composable("galleryScreen") { PhotoGalleryScreen(navController) }
        composable("appContent") { AppContent(navController) }
    }
}
