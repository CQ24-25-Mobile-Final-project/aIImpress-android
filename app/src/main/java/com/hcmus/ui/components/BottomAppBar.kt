package com.hcmus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import com.google.accompanist.insets.LocalWindowInsets
import com.hcmus.R

@Composable
fun CustomBottomBar(
    onAddClick: () -> Unit,
    onTabSelected: (Int) -> Unit,
    selectedIndex: Int,
    navController: NavController
) {
    // Get system insets for bottom navigation


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp) // Height of the Bottom Bar
            .background(Color.White),

        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bottom bar items
            BottomBarItem(
                isSelected = selectedIndex == 0,
                iconRes = R.drawable.gallery_icon,
                contentDescription = "Gallery",
                onClick = {
                    onTabSelected(0)
                    navController.navigate("gallery")
                }
            )
            BottomBarItem(
                isSelected = selectedIndex == 1,
                iconRes = R.drawable.album_icon,
                contentDescription = "Album",
                onClick = {
                    onTabSelected(1)
                    navController.navigate("MyAlbumScreen")
                }
            )

            Spacer(modifier = Modifier.width(64.dp))

            BottomBarItem(
                isSelected = selectedIndex == 2,
                iconRes = R.drawable.privacy_icon,
                contentDescription = "Share",
                onClick = {
                    onTabSelected(2)
                }
            )
            BottomBarItem(
                isSelected = selectedIndex == 3,
                iconRes = R.drawable.people_icon,
                contentDescription = "Profile",
                onClick = {
                    onTabSelected(3)
                }
            )
        }

        // Floating action button
        FloatingActionButton(
            onClick = onAddClick,
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(52.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-10).dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.camera_icon),
                contentDescription = "Camera Icon",
                tint = Color.White,
                modifier = Modifier.size(27.dp)
            )
        }
    }
}

@Composable
fun BottomBarItem(
    isSelected: Boolean,
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(50.dp) // Kích thước của từng mục
            .clip(CircleShape) // Hình dạng tròn
            .background(if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent) // Màu nền nếu được chọn
            .clickable(onClick = onClick), // Xử lý click
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary, // Màu sắc khi được chọn
            modifier = Modifier.size(27.dp) // Kích thước biểu tượng
        )
    }
}
