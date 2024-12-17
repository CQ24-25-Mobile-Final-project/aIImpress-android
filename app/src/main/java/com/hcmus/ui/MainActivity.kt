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
import dagger.hilt.android.AndroidEntryPoint
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.resolveDefaults
import androidx.core.app.ActivityCompat
import com.hcmus.auth.AuthResponse
import com.hcmus.auth.AuthenticationManager
import com.hcmus.ui.screens.SignInScreen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

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
      LoginScreen(onLoginSuccess = {
        navController.navigate("gallery") {
          popUpTo("login") { inclusive = true }
        }
      }, onLoginEmail = { email, password ->
        authManager.loginWithEmail(email, password).onEach { response ->
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
          }.launchIn(coroutineScope)
      }, onSignIn = {
        navController.navigate("signIn") {
          popUpTo("login") { inclusive = true }
        }
      }, onLoginGoogle = {
        authManager.signInWithGoogle().onEach { response ->
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
          }.launchIn(coroutineScope)
      })
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
                  Toast.makeText(context, "Account created successfully", Toast.LENGTH_SHORT).show()
                  navController.navigate("login")
                }
                is AuthResponse.Error -> {
                  Log.e("Login", "Account creation failed: ${response.message}")
                  Toast.makeText(context, "Failed: ${response.message}", Toast.LENGTH_LONG).show()
                }
              }
            }
            .launchIn(coroutineScope)
        }
      )
    }

    // Main App Screens
    composable("gallery") { PhotoGalleryScreen(navController) }
    composable("authentication") { AuthenticationScreen(navController) }
    composable("view") {
      SecretPhotoViewScreen(onBackPressed = {
        navController.navigate("gallery") {
          popUpTo("gallery") { inclusive = true } // Clear intermediate screens
        }
      })
    }

    composable(
      route = "imageDetail/{photoUri}", arguments = listOf(navArgument("photoUri") {
        type = NavType.StringType
      })
    ) { backStackEntry ->
      val photoUri = backStackEntry.arguments?.getString("photoUri") ?: ""
      ImageDetailScreen(photoUri = photoUri, navController = navController)
    }

    composable(
      route = "editImage/{photoUri}", arguments = listOf(navArgument("photoUri") {
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
