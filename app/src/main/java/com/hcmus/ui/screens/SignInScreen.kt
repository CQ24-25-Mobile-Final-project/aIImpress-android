package com.hcmus.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hcmus.ui.theme.BluePrimary
import com.hcmus.ui.theme.MyApplicationTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.hcmus.R

@Composable
fun SignInScreen(
  onSignIn: (email: String, password: String) -> Unit,
) {
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }
  val context = LocalContext.current


  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {

    Spacer(modifier = Modifier.height(24.dp))

    // App Logo and Tagline
    Column(
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Image(
        painter = painterResource(id = R.drawable.app_logo),
        contentDescription = "Logo Icon",
        modifier = Modifier.size(150.dp)
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Sign-In Buttons
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Email") },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
      )

      OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Password") },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = PasswordVisualTransformation()
      )

      OutlinedTextField(
        value = confirmPassword,
        onValueChange = { confirmPassword = it },
        label = { Text("Confirm Password") },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = PasswordVisualTransformation()
      )

      Spacer(modifier = Modifier.height(24.dp))

      SignInButton(
        text = "Sign Up",
        color = BluePrimary,
        onClick = {
          when {
            email.isEmpty() -> {
              Toast.makeText(context, "Email cannot be empty", Toast.LENGTH_SHORT).show()
            }
            password.isEmpty() -> {
              Toast.makeText(context, "Password cannot be empty", Toast.LENGTH_SHORT).show()
            }
            password.length < 6 -> {
              Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
              Toast.makeText(context, "Invalid email format", Toast.LENGTH_SHORT).show()
            }
            !checkPassword(password, confirmPassword) -> {
              Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
            else -> {
              onSignIn(email, password)
            }
          }
        }
      )
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}

fun checkPassword(password: String, confirmPassword: String): Boolean {
  return password == confirmPassword
}


// previews
@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
  MyApplicationTheme {
    SignInScreen({} as (String, String) -> Unit)
  }
}
