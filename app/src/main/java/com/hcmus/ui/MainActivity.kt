package com.hcmus.ui

import android.Manifest
import android.os.Build
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
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.hcmus.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.app.ActivityCompat
import com.hcmus.auth.AuthResponse
import com.hcmus.auth.AuthenticationManager
import com.hcmus.ui.screens.SignInScreen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

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
    enableEdgeToEdge()

    val permissions = if (Build.VERSION.SDK_INT >= 33) {
      arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
      arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    ActivityCompat.requestPermissions(this, permissions, 0)

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
  val context = LocalContext.current

  val authManager = remember {
    AuthenticationManager(context)
  }
  val coroutineScope = rememberCoroutineScope()

  NavHost(navController = navController, startDestination = "login") {
    composable("login") {
      LoginScreen(
        onLoginSuccess = {
          navController.navigate("gallery") {
            popUpTo("login") { inclusive = true }
          }
        },
        onLoginEmail = { email, password ->
          authManager.loginWithEmail(email, password)
            .onEach { response ->
              if (response is AuthResponse.Success) {
                Log.d("Login", "Success: $response")
                Toast.makeText(context, "Login Success", Toast.LENGTH_SHORT).show()
                navController.navigate("gallery") {
                  popUpTo("login") { inclusive = true }
                }
              } else {
                Log.d("Login", "Error: $response")
                Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
              }
            }
            .launchIn(coroutineScope)
        },
        onSignIn = {
          navController.navigate("signIn") {
            popUpTo("login") { inclusive = true }
          }
        }
      )
    }

    composable("signIn") {
      SignInScreen(
        onSignIn = { email, password ->
          Log.d("Login", "Creating account with email:$email, pass:$password")

          authManager.createAccountWithEmail(email, password)
            .onEach { response ->
              if (response is AuthResponse.Success) {
                Toast.makeText(context, "Create Account Success", Toast.LENGTH_SHORT).show()
                navController.navigate("login")
              } else {
                Toast.makeText(context, "Create Account Failed", Toast.LENGTH_SHORT).show()
              }
            }
            .launchIn(coroutineScope)
        },
      )
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
