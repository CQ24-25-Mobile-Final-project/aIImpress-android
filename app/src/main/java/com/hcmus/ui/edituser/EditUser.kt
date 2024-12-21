package com.hcmus.ui.edituser

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.hcmus.ui.components.CustomBottomBar // Kiểm tra xem nơi khai báo
import com.hcmus.R

@Composable
fun ProfileScreen(navController: NavController) {
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
            ProfileHeader()
            ProfileBody(navController)
        }
    }
}


@Composable
fun ProfileHeader() {
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
                // Profile Picture Placeholder
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with actual image resource
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .fillMaxSize()
                )
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
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    ProfileScreen(navController = rememberNavController())
}
