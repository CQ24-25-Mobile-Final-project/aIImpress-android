package com.hcmus.ui.edituser

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.hcmus.ui.components.CustomBottomBar
import com.hcmus.R
import com.hcmus.data.model.CredentialDatabase
import com.hcmus.data.model.Gender
import com.hcmus.data.model.Profile
import com.hcmus.ui.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController,profileViewModel: ProfileViewModel = viewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val profileState = remember { mutableStateOf<Profile?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Get the current user's email
    val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

    // Load profile data when the screen is displayed
    LaunchedEffect(currentUserEmail) {
        coroutineScope.launch {
            currentUserEmail?.let { email ->
                val profile = profileViewModel.getProfileByEmail(email)
                profileState.value = profile
                selectedImageUri = profile?.avatarUrl?.let { Uri.parse(it) }
            }
        }
    }

    Scaffold(
        bottomBar = {
            CustomBottomBar(
                selectedIndex = 0,
                onTabSelected = { index -> },
                onAddClick = { navController.navigate("appContent") },
                navController = navController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F8F8))
        ) {
            profileState?.let {
                ProfileHeader(it.value?.avatarUrl.toString())
                ProfileBody(navController)
            }
        }
    }
}


@Composable
fun ProfileHeader(selectedImageUri: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(Color(0xFFEEF2F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            ) {
                if (selectedImageUri != null && selectedImageUri!!.isNotEmpty()) {
                    Image(
                        painter = rememberImagePainter(data = selectedImageUri),
                        contentDescription = "Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Profile Image",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Icon(
                    painter = painterResource(id = R.drawable.edit_icon), // Replace with pencil icon resource
                    contentDescription = "Edit",
                    tint = Color.Black,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp)
                        .background(Color.White, CircleShape)
                        .padding(4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Puerto Rico",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun ProfileBody() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ProfileOptionSection("Edit profile information")
        ProfileOptionSection(
            title = "Notifications",
            action = { Text(text = "ON", color = MaterialTheme.colorScheme.primary) }
        )
        ProfileOptionSection(
            title = "Language",
            action = { Text(text = "English", color = MaterialTheme.colorScheme.primary) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        ProfileOptionSection("Security")
        ProfileOptionSection(
            title = "Theme",
            action = { Text(text = "Light mode", color = MaterialTheme.colorScheme.primary) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        ProfileOptionSection("Help & Support")
        ProfileOptionSection("Contact us")
        ProfileOptionSection("Privacy policy")
    }
}

@Composable
fun ProfileOptionSection(
    title: String,
    action: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 16.sp, color = Color.Black)
        action?.invoke()
    }
}
@Composable
fun ProfileBody(navController: NavController) {
    val credentialRepository = CredentialDatabase.getInstance(LocalContext.current).credentialRepository()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ProfileOptionSection(
            title = "Edit profile information",
            onClick = { navController.navigate("editProfile") }
        )
        ProfileOptionSection(
            title = "Notifications",
            action = { Text(text = "ON", color = MaterialTheme.colorScheme.primary) }
        )
        ProfileOptionSection(
            title = "Language",
            action = { Text(text = "English", color = MaterialTheme.colorScheme.primary) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        ProfileOptionSection("Security")
        ProfileOptionSection(
            title = "Theme",
            action = { Text(text = "Light mode", color = MaterialTheme.colorScheme.primary) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        ProfileOptionSection("Help & Support")
        ProfileOptionSection("Contact us")
        ProfileOptionSection("Privacy policy")
        Spacer(modifier = Modifier.height(8.dp))

        ProfileOptionSection(
            title = "Logout",
            onClick = {
                credentialRepository.delete()
                navController.navigate("login")
            }
        )

    }
}


@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    ProfileScreen(navController = rememberNavController())
}
