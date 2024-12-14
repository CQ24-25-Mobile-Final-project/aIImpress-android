package com.hcmus.ui.secret

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationScreen(navController: NavHostController) {
    var passcode by remember { mutableStateOf("") }

    LaunchedEffect(passcode) {
        if (passcode.length == 4) {
            navController.navigate("view")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Passcode") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("main") }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Lock Icon",
                modifier = Modifier
                    .size(90.dp)
                    .padding(top = 20.dp),
                tint = Color.Black

            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Enter your 4 digit Passcode",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(4) { index ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (index < passcode.length) "●" else "○",
                            fontSize = 30.sp,
                            color = Color.Black
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            NumericKeypad(
                onNumberClicked = { number ->
                    if (passcode.length < 4) passcode += number
                },
                onBackspace = {
                    if (passcode.isNotEmpty()) passcode = passcode.dropLast(1)
                }
            )
        }
    }
}

@Composable
fun NumericKeypad(
    onNumberClicked: (String) -> Unit,
    onBackspace: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("Forget", "0", "⟵")
        )
        keys.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                row.forEach { key ->
                    Button(
                        onClick = {
                            when (key) {
                                "⟵" -> onBackspace()
                                "Forget" -> {} // Add action for "Forget" if needed
                                else -> onNumberClicked(key)
                            }
                        },
                        modifier = Modifier.size(80.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (key == "Forget") Color.Transparent else Color.White
                        )
                    ) {
                        Text(
                            text = key,
                            fontSize = if (key in "0123456789⟵") 30.sp else 20.sp,
                            color = if (key == "Forget") Color.Gray else Color.Black
                        )
                    }
                }
            }
        }

    }
}
