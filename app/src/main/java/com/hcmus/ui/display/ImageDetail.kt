package com.hcmus.ui.display

import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.util.Log
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.hcmus.R
import com.hcmus.ui.textrecognize.TextRecognitionResultBar
import com.hcmus.ui.textrecognize.recognizeText
import com.hcmus.ui.album.AlbumViewModel
import com.hcmus.ui.components.MediaFileManager
import com.hcmus.ui.components.getPhotoDetail
import showMoreOptions
import com.hcmus.ui.components.addTag1 as addTag1


@Composable
fun ImageDetailScreen(photoUri: String, navController: NavController) {
    Log.d("ImageGalleryScreen", "Calling SmartAlbumOrganizer with URI: $photoUri")

    val decodedUri = Uri.decode(photoUri)
    val showResult = remember { mutableStateOf(false) }
    val recognizedText = remember { mutableStateOf("") }
    val context = LocalContext.current

    val albumViewModel: AlbumViewModel = hiltViewModel()
    val photoViewModel: PhotoViewModel = viewModel(
        factory = PhotoViewModelFactory(context)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Top bar
        DetailTopBar(navController, photoUri = photoUri, albumViewModel, photoViewModel)

        // Box to overlay button or text result on image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // Display the image
            Image(
                painter = rememberAsyncImagePainter(model = Uri.parse(photoUri)),
                contentDescription = "Image Detail",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )


            if (!showResult.value) {
                // Button overlay (only show when no result)
                Button(
                    onClick = {
                        recognizeText(context, photoUri) { result ->
                            recognizedText.value = result
                            showResult.value = true // Show the result and hide the button
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(text = "Copy text in image", color = Color.White)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    // Button to copy text, placed above the TextRecognitionResultBar
                    Button(
                        onClick = {
                            copyTextToClipboard(context, recognizedText.value)
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally) // Center the button horizontally
                            .padding(16.dp), // Add padding to give space around the button
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray.copy(alpha = 0.5f))
                    ) {
                        Text(text = "Copy Text", color = Color.White)
                    }

                    // Add space between the button and the result text
                    Spacer(modifier = Modifier.height(1.dp)) // Adjust the height as needed

                    // Display recognized text in the TextRecognitionResultBar
                    TextRecognitionResultBar(
                        showResult = showResult.value,
                        recognizedText = recognizedText.value
                    )
                }

            }

        }

        // Bottom bar
        DetailBottomBar(navController = navController, photoUri = photoUri)
    }
}


// TopBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTopBar(navController: NavController, photoUri: String, albumViewModel: AlbumViewModel, photoViewModel: PhotoViewModel) {
    val isHeartPressed = remember { mutableStateOf(false) }
    val isTagPressed = remember { mutableStateOf(false) }
    val context = LocalContext.current

    val photoDetail = photoViewModel.photos.value?.find { it.uri.toString() == photoUri }
    if (photoDetail != null) {
        if (photoDetail.tag != "") {
            isTagPressed.value = true
        }
    }

    val isFavorite = albumViewModel.albums.value?.any { it.first == "Favorite" && it.second.contains(Uri.parse(photoUri)) } == true
    isHeartPressed.value = isFavorite
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

                Icon(
                    painter = painterResource(id = if (isTagPressed.value) R.drawable.activetag else R.drawable.tag),
                    contentDescription = "Tag Icon",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }

                        ) {
                            isTagPressed.value = !isTagPressed.value
                            Log.d("DetailTopBar", "isTagPressed value: ${isTagPressed.value}")
                            Log.d("DetailTopBar", "photoUri value: $photoUri")
                            photoViewModel.addTag( photoUri, "Favorite" )
                        },
                    tint = if (isTagPressed.value) Color.Yellow else Color.Gray
                )

                Spacer(modifier = Modifier.width(10.dp))

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
                            if (isHeartPressed.value) {
                                albumViewModel.deletePhotoInAlbum("Favorite", Uri.parse(photoUri))
                            } else {
                                albumViewModel.addToFavorite(Uri.parse(photoUri))
                            }
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
            onClick = { selectedItem.value = 1
            navController.navigate("trash_album_screen")}
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
fun copyTextToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = android.content.ClipData.newPlainText("Recognized Text", text)
    clipboard.setPrimaryClip(clip)
}
