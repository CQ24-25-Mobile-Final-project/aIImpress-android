package com.hcmus.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hcmus.ui.theme.BluePrimary
import com.hcmus.ui.theme.MyApplicationTheme

val btnColor = ButtonColors(
  containerColor = BluePrimary,
  contentColor = Color.White,
  disabledContainerColor = Color.White,
  disabledContentColor = Color.White
)

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
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
      Text(
        text = "WALPER",
        style = TextStyle(
          color = BluePrimary, // Adjusted to match the logo color
          fontSize = 28.sp
        )
      )
      Text(
        text = "Only Wallpaper app you will ever need ...!",
        style = TextStyle(
          color = Color.Gray,
          fontSize = 14.sp
        ),
        modifier = Modifier.padding(top = 8.dp)
      )
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Sign-In Buttons
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      SignInButton(
        text = "Continue With Email",
        color = BluePrimary,
        icon = Icons.Default.AccountBox,
        onClick = onLoginSuccess
      )

      Spacer(modifier = Modifier.height(16.dp))

      SignInButton(
        text = "Continue With Google",
        color = Color.White,
        icon = Icons.Default.AccountBox,
        onClick = onLoginSuccess
      )

      Spacer(modifier = Modifier.height(16.dp))

      SignInButton(
        text = "Continue With Facebook",
        color = Color(0xFF4267B2),
        icon = Icons.Default.AccountBox,
        onClick = onLoginSuccess
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = "Already Have An Account? Sign In",
      color = BluePrimary,
      modifier = Modifier.padding(top = 16.dp)
    )
  }
}

@Composable
fun SignInButton(
  text: String,
  color: Color,
  icon: ImageVector? = null,
  onClick: () -> Unit
) {
  // TODO: add icon
  Button(
    onClick = onClick,
    modifier = Modifier
      .fillMaxWidth()
      .height(48.dp),
    colors = ButtonDefaults.buttonColors(containerColor = color)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Text(
        text = text,
        color = if (color == Color.White) Color.Black else Color.White
      )
    }
  }
}


// previews
@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
  MyApplicationTheme {
    LoginScreen { }
  }
}
