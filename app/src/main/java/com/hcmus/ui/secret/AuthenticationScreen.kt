package com.hcmus.ui.secret

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationScreen(navController: NavHostController) {
    var passcode by remember { mutableStateOf("") }

    val context = LocalContext.current

    LaunchedEffect(passcode) {
        if (passcode.length == 4) {
            val savedPin = PinStorage.getPin(context)
            if (passcode == savedPin) {
                // Điều hướng khi mã PIN đúng
                navController.navigate("view")
            } else {
                Toast.makeText(context,"Mật khẩu không đúng. Vui lòng nhập lại.",Toast.LENGTH_SHORT).show()
                passcode = ""
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Passcode") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("gallery") }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back",tint=Color.Black)
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
                tint=Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Enter your 4 digit Passcode",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color=Color.Black
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
                },
                keyLabel = "Forget"// dùng để đánh dấu nhãn của các nút tránh viết lại nhiều lần
            )
        }
    }
}
