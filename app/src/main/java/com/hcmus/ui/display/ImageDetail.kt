package com.hcmus.ui.display

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.hcmus.R
import showMoreOptions

// Image Detail Screen
@Composable
fun ImageDetailScreen(photoUri: String, navController: NavController) {
    val decodedUri = Uri.decode(photoUri)
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Top bar
        DetailTopBar(navController)

        // Display the image
        Image(
            painter = rememberAsyncImagePainter(model = Uri.parse(photoUri)),
            contentDescription = "Image Detail",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentScale = ContentScale.Crop
        )

        // Bottom bar
        DetailBottomBar(navController = navController, photoUri = photoUri)
    }
}

// TopBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTopBar(navController: NavController) {
    val isHeartPressed = remember { mutableStateOf(false) }
    TopAppBar(
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back Icon
                Icon(
                    painter = painterResource(id = R.drawable.back_icon),
                    contentDescription = "Back Icon",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            navController.popBackStack()
                        }
                )

                // Title
                Text(
                    text = "Photo Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                // Heart Icon
                Icon(
                    painter = painterResource(id = if (isHeartPressed.value) R.drawable.heartactive_icon else R.drawable.heart_icon),
                    contentDescription = "Heart Icon",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            isHeartPressed.value = !isHeartPressed.value
                        },
                    tint = if (isHeartPressed.value) Color.Red else Color.Gray
                )
            }
        },
        /*colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )*/
    )
}



// BottomBar with Navigation
@Composable
fun DetailBottomBar(navController: NavController, photoUri: String) {
    val selectedItem = remember { mutableStateOf(0) }
    val showDialog = remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.White),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomBarItem(
            isSelected = selectedItem.value == 0,
            iconRes = R.drawable.privacy_icon,
            contentDescription = "Privacy Icon",
            onClick = { selectedItem.value = 0 }
        )

        BottomBarItem(
            isSelected = selectedItem.value == 1,
            iconRes = R.drawable.trash_icon,
            contentDescription = "Delete Icon",
            onClick = { selectedItem.value = 1 }
        )

        BottomBarItem(
            isSelected = selectedItem.value == 2,
            iconRes = R.drawable.edit_icon,
            contentDescription = "Edit Icon",
            onClick = {
                selectedItem.value = 2
                navController.navigate("editImage/${Uri.encode(photoUri)}")
            }

        )

        BottomBarItem(
            isSelected = selectedItem.value == 3,
            iconRes = R.drawable.share_icon,
            contentDescription = "Share Icon",
            onClick = { selectedItem.value = 3 },

        )

        BottomBarItem(
            isSelected = selectedItem.value == 4,
            iconRes = R.drawable.menu_icon,
            contentDescription = "More Icon",
            onClick = {
                selectedItem.value = 4
                showDialog.value = true },

        )
        BottomBarItem(
            isSelected = selectedItem.value == 5,
            iconRes = R.drawable.menu_icon,
            contentDescription = "More Icon",
            onClick = {
                selectedItem.value = 5
                navController.navigate("imageDescription/${Uri.encode(photoUri)}")
                },

            )
    }
    if (showDialog.value) {
        showMoreOptions(navController, photoUri, context = LocalContext.current)
    }
}

// Bottom Bar Item
@Composable
fun BottomBarItem(
    isSelected: Boolean,
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    backgroundColor: Color = Color.Transparent
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(if (isSelected) MaterialTheme.colorScheme.background else backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(27.dp)
        )
    }
}
