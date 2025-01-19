package com.hcmus.presentation.screens

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.hcmus.domain.Constants
import com.hcmus.domain.Screen
import com.hcmus.presentation.AiGenerateImageViewModel
import com.hcmus.ui.theme.MyApplicationTheme

@Composable
fun ImageScreen(viewModel: AiGenerateImageViewModel, navController: NavHostController) {

    val imageUrl by viewModel.imageUrl.collectAsState()
    val errorMessage by viewModel.error.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(imageUrl) {
        Log.d(Constants.TAG, "Inside Launched Effect")
        imageUrl?.let { Log.d(Constants.TAG, "Image String: $it") }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Photo in",
            contentScale = ContentScale.Fit,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    navController.navigate(Screen.HomeScreen.route) {
                        popUpTo(Screen.HomeScreen.route) { inclusive = false }
                    }
                },
                modifier = Modifier.padding(
                    horizontal = 6.dp
                )
            ) {
                Text(text = "Back", color = Color.White)
            }
        }

        errorMessage?.let { error ->
            Text(text = "Error: $error", fontSize = 16.sp, color = Color.Red)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MyApplicationTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Preview of Image Screen", fontSize = 20.sp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { },
                    modifier = Modifier.padding(
                        horizontal = 6.dp
                    )
                ) {
                    Text(text = "Back", color = Color.White)
                }

                Button(
                    onClick = {

                    },
                    modifier = Modifier.padding(
                        horizontal = 6.dp
                    )
                ) {
                    Text(text = "Save", color = Color.White)
                }
            }
        }
    }
}
