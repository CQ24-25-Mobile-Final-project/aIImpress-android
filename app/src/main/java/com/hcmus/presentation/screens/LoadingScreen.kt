package com.hcmus.presentation.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import com.hcmus.domain.Constants
import com.hcmus.domain.Screen
import com.hcmus.presentation.AiGenerateImageViewModel

@Composable
fun LoadingScreen(viewModel: AiGenerateImageViewModel, navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
        val imageString by viewModel.imageUrl.collectAsState()
        Log.d("GenerateImageScreen", imageString.toString())
        if(imageString != Constants.LOADING){
            navController.navigate(Screen.ImageScreen.route) {
                popUpTo(Screen.ImageScreen.route) { inclusive = false }
            }
        }
    }
}