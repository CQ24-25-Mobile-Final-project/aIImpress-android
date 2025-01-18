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
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationScreen(navController: NavHostController) {
    var passcode by remember { mutableStateOf("") }
    val context = LocalContext.current as FragmentActivity
    val scope = rememberCoroutineScope()

    var isAuthFailed by remember { mutableStateOf(false) }
    var isUnLocking by remember { mutableStateOf(false) }
    val biometricPromptManager = remember { BiometricPromptManager(context) }

    LaunchedEffect(passcode) {
        if (passcode.length == 4) {
            val savedPin = PinStorage.getPin(context)
            if (passcode == savedPin) {
                isUnLocking = true
                isAuthFailed=false
                navController.navigate("view")
            } else {
                Toast.makeText(context, "Mật khẩu không đúng. Vui lòng nhập lại.", Toast.LENGTH_SHORT).show()
                passcode = ""
            }
        }
    }

    LaunchedEffect(Unit) {
        biometricPromptManager.promptResults.collect { result ->
            when (result) {
                is BiometricPromptManager.BiometricResult.AuthenticationSuccess -> {
                    isUnLocking = true
                    isAuthFailed = false
                    navController.navigate("view")
                }
                is BiometricPromptManager.BiometricResult.AuthenticationError -> {
                    isAuthFailed = true
                    Toast.makeText(context, "Lỗi xác thực: ${result.error}", Toast.LENGTH_SHORT).show()
                }
                is BiometricPromptManager.BiometricResult.AuthenticationFailed -> {
                    isAuthFailed = true
                    Toast.makeText(context, "Xác thực không thành công", Toast.LENGTH_SHORT).show()
                }
                is BiometricPromptManager.BiometricResult.HardwareUnavailable -> {
                    Toast.makeText(context, "Phần cứng không khả dụng", Toast.LENGTH_SHORT).show()
                }
                is BiometricPromptManager.BiometricResult.FeatureNotSupported -> {
                    Toast.makeText(context, "Tính năng không được hỗ trợ", Toast.LENGTH_SHORT).show()
                }
                is BiometricPromptManager.BiometricResult.AuthenticationCanceled -> {
                    Toast.makeText(context, "Xác thực bị hủy", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Passcode") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("gallery") }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
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
                imageVector = when {
                    isUnLocking -> Icons.Default.CheckCircle // Icon mở khóa khi thành công
                    isAuthFailed -> Icons.Default.Lock // Icon khóa khi thất bại
                    else -> Icons.Default.Face // Icon khuôn mặt khi đang chờ xác thực
                },
                contentDescription = when {
                    isUnLocking -> "Unlocked Icon"
                    isAuthFailed -> "Locked Icon"
                    else -> "Face Recognition Icon"
                },
                modifier = Modifier
                    .size(90.dp)
                    .padding(top = 20.dp)
                    .clickable {
                        if (!isUnLocking) {
                            scope.launch {
                                biometricPromptManager.showBiometricPrompt(
                                    title = "Xác thực bằng vân tay",
                                    description = "Sử dụng vân tay của bạn để mở khóa"
                                )
                            }
                        }
                    },
                tint = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isUnLocking) "Đã mở khóa!" else "Nhập mật khẩu 4 chữ số",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(4) { index ->
                    Box(
                        modifier = Modifier
                            .size(40.dp),

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
                keyLabel = "Forget"
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    scope.launch {
                        biometricPromptManager.showBiometricPrompt(
                            title = "Mở bằng Vân Tay",
                            description = "Sử dụng vân tay của bạn để mở khóa"
                        )
                    }
                }
            ) {
                Text(text = "Unlock with Fingerprint", color = Color.White)
            }
        }
    }
}
