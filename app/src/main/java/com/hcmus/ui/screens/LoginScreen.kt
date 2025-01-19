package com.hcmus.ui.screens

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hcmus.ui.theme.BluePrimary
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.hcmus.R

@Composable
fun LoginScreen(
  onLoginEmail: (email: String, password: String) -> Unit,
  onSignIn: () -> Unit,
  onLoginGoogle: (FirebaseUser?) -> Unit
) {
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  val context = LocalContext.current
  val auth = FirebaseAuth.getInstance()

  // Configure Google Sign-In options for Firebase
  val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
    .requestIdToken("679823525557-t7h5r3bv2f2hqvosfro990lb9momk7g9.apps.googleusercontent.com") // Replace with your Firebase client ID
    .requestEmail()
    .build()

  val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)

  val googleSignInLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
  ) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
      val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
      handleSignInResult(task, auth, onLoginGoogle)
    } else {
      onLoginGoogle(null)
    }
  }

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

      Spacer(modifier = Modifier.height(24.dp))

      SignInButton(
        text = "Login",
        color = BluePrimary,
        onClick = { onLoginEmail(email, password) }
      )

      Text(
        text = "Or",
        color = Color.Black,
        modifier = Modifier.padding(top = 16.dp)
      )

      Spacer(modifier = Modifier.height(16.dp))

      SignInButton(
        text = "Continue With Google",
        color = Color.Transparent,
        iconPainter = painterResource(id = R.drawable.google_icon), // Icon Google
        border = true, // Hiển thị border
        onClick = {
          val signInIntent = googleSignInClient.signInIntent
          googleSignInLauncher.launch(signInIntent)  // Launch Google Sign-In
        }
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(
      modifier = Modifier
        .padding(top = 16.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Already Have An Account? ",
        color = Color.Gray
      )
      Text(
        text = "Sign Up",
        color = BluePrimary,
        modifier = Modifier.clickable { onSignIn() },
        style = TextStyle(
          fontWeight = FontWeight.Bold
        )
      )
    }
  }
}

@Composable
fun SignInButton(
  text: String,
  color: Color,
  icon: ImageVector? = null,
  iconPainter: Painter? = null,
  border: Boolean = false,
  onClick: () -> Unit
) {
  Button(
    onClick = onClick,
    modifier = Modifier
      .fillMaxWidth()
      .height(48.dp),
    colors = ButtonDefaults.buttonColors(containerColor = color),
    shape = RoundedCornerShape(100.dp), // Góc bo của button
    border = if (border) BorderStroke(1.dp, BluePrimary) else null
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      // Nếu có icon thì hiển thị icon trước text
      if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          modifier = Modifier
            .size(40.dp)
            .padding(end = 8.dp),
          tint = if (color == Color.White) BluePrimary else Color.White
        )
      } else if (iconPainter != null) {
        Image(
          painter = iconPainter,
          contentDescription = null,
          modifier = Modifier
            .size(24.dp)
            .padding(end = 8.dp)
        )
      }

      Text(
        text = text,
        color = if (color == Color.Transparent) BluePrimary else Color.White
      )
    }
  }
}

private fun handleSignInResult(
  completedTask: Task<GoogleSignInAccount>,
  auth: FirebaseAuth,
  onLoginGoogle: (FirebaseUser?) -> Unit
) {
  try {
    // Đăng nhập với tài khoản Google
    val account = completedTask.getResult(ApiException::class.java)
    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

    // Đăng nhập hoặc đăng ký người dùng với Firebase
    auth.signInWithCredential(credential)
      .addOnCompleteListener { task ->
        if (task.isSuccessful) {

          // Đăng nhập hoặc tạo tài khoản thành công
          val user = auth.currentUser
          Log.d("DebugInfo", "Email: $user")

          onLoginGoogle(user)  // Pass FirebaseUser
        } else {
          // Nếu đăng nhập thất bại
          onLoginGoogle(null)
        }
      }
  } catch (e: ApiException) {
    Log.e("GoogleSignIn", "signInResult:failed code=" + e.statusCode)
    onLoginGoogle(null)  // Nếu gặp lỗi trong quá trình đăng nhập
  }
}

