package com.example.photoapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.photoapp.CustomBottomBar
import com.example.photoapp.GalleryTopBar
import com.example.photoapp.R
import com.example.photoapp.ui.theme.PhotoappTheme

// Data class for Album
data class Album(
    val name: String,
    val imageRes: Int // ID của hình ảnh tương ứng với album
)

// Sample data for albums
val albumList = listOf(
    Album("Party shots", R.drawable.avatar),
    Album("Photoshoots", R.drawable.avatar),
    Album("Graduation", R.drawable.avatar),
    Album("Travel", R.drawable.avatar)
)

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "gallery"
    ) {
        composable("gallery") { SharedGalleryScreen(navController) }
        composable("share_screen") { ShareScreen() }
    }
}

@Composable
fun SharedGalleryScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F8FC)) // Background color
    ) {
        // Reuse GalleryTopBar
        GalleryTopBar()

        // Content Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .weight(1f), // Push CustomBottomBar to the bottom
        ) {
            // Buttons Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { /* Handle Create Shared Album */ },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEAF6FE)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Create shared album",
                        color = Color(0xFF1B87C9),
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { /* Handle View Conversation */ },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEAF6FE)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "View conversation",
                        color = Color(0xFF1B87C9),
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Shared People Section
            Text(
                text = "Shared People",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(listOf("jerry@gmail.com", "henry@gmail.com", "ben@gmail.com", "elisa@gmail.com", "kelvin@gmail.com")) { email ->
                    SharedPerson(email = email)
                }
            }

            // Shared Albums Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Shared Albums",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                Text(
                    text = "View all",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF1B87C9),
                    modifier = Modifier.clickable { /* Navigate to album view */ }
                )
            }

            // Display albums using LazyVerticalGrid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(albumList) { album ->
                    SharedAlbumCard(album = album, navController = navController)
                }
            }
        }

        // Reuse CustomBottomBar
        CustomBottomBar(
            onAddClick = { /* Handle Add Action */ },
            onTabSelected = { /* Handle Tab Change */ },
            selectedIndex = 3, // Profile tab as selected
            navController = navController
        )
    }
}

@Composable
fun SharedAlbumCard(album: Album, navController: NavController) {
    Box(
        modifier = Modifier
            .aspectRatio(1f) // Tỉ lệ hình vuông
            .background(Color.Gray) // Placeholder cho album thumbnail
    ) {
        // Hiển thị hình ảnh album từ danh sách
        Image(
            painter = painterResource(id = album.imageRes), // Dùng ảnh từ danh sách
            contentDescription = "Album Image",
            modifier = Modifier
                .fillMaxSize(),// Đảm bảo ảnh chiếm toàn bộ diện tích Box
            contentScale = ContentScale.Crop // Phóng to hình ảnh để phủ đầy Box
        )

        // Nội dung của album
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = album.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = painterResource(id = R.drawable.people_icon),
                    contentDescription = "People Count",
                    tint = Color.White,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { navController.navigate("share_screen") } // Navigate on click
                )
                Text(
                    text = "12", // Replace with dynamic count
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
            }
        }
    }
}


@Composable
fun SharedPerson(email: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.Gray), // Placeholder cho avatar
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.share_avatar), // Đảm bảo tài nguyên avatar hợp lệ
                contentDescription = "Avatar",
                modifier = Modifier
                    .fillMaxSize() // Đảm bảo hình ảnh chiếm hết Box
                    .clip(CircleShape) // Giữ hình tròn cho avatar
            )
        }
        Text(
            text = email,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun ShareScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F8FC)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Share Screen",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SharePreview() {
    PhotoappTheme {
        val navController = rememberNavController()
        AppNavHost(navController)
    }
}
