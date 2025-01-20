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
fun CreatePinScreen(navController: NavHostController) {
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(0) } // 0: nhập PIN, 1: xác nhận PIN
    val context = LocalContext.current

    // Tự động chuyển sang bước xác nhận khi đủ 4 số
    LaunchedEffect(pin) {
        if (pin.length == 4 && step == 0) {
            confirmPin = pin
            pin = ""
            step = 1
        }
    }

    // Tự động chuyển qua view khi mã PIN xác nhận khớp
    LaunchedEffect(pin) {
        if (step == 1 && pin.length == 4) {
            if (pin == confirmPin) {
                // Thực hiện lưu mã PIN vào database hoặc logic khác
                PinStorage.savePin(context, pin)
                navController.navigate("view")
            } else {
                // Hiển thị Toast thông báo lỗi nếu mã xác nhận không đúng
                Toast.makeText(context, "Mã xác nhận không đúng. Vui lòng nhập lại.", Toast.LENGTH_SHORT).show()
                pin = ""  // Đặt lại pin để người dùng nhập lại
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Pin") },
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
                text = if (step == 0) "Create your 4 digit Pin" else "Confirm your Pin",
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
                            text = if (index < pin.length) "●" else "○",
                            fontSize = 30.sp,

                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            NumericKeypad(
                onNumberClicked = { number ->
                    if (pin.length < 4) pin += number
                },
                onBackspace = {
                    if (pin.isNotEmpty()) pin = pin.dropLast(1)
                },
                keyLabel = "Forget"
            )

        }
    }
}

@Composable
fun NumericKeypad(
    onNumberClicked: (String) -> Unit,
    onBackspace: () -> Unit,
    keyLabel: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf(keyLabel, "0", "⌫")
        )
        keys.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                row.forEach { key ->
                    Button(
                        onClick = {
                            when (key) {
                                "⌫" -> onBackspace()
                                keyLabel -> {} // Thêm logic nếu cần
                                else -> onNumberClicked(key)
                            }
                        },
                        modifier = Modifier.size(80.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (key == keyLabel) Color.Transparent else Color.White
                        )
                    ) {
                        Text(
                            text = key,
                            fontSize = if (key in "0123456789⌫") 30.sp else 20.sp,
                        )
                    }
                }
            }
        }
    }
}
