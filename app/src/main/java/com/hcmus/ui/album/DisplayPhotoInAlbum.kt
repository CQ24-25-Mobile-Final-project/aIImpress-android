package com.hcmus.ui.album

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.hcmus.ui.components.MyTopAppBar
import com.hcmus.R
import com.hcmus.ui.theme.MyApplicationTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayPhotoInAlbum(navController: NavController) {
    val photos = listOf(R.drawable.wallpaper, R.drawable.photo2, R.drawable.photo3)
    val selectedPhotos = remember { mutableStateListOf<Int>() }
    val isSelectedDropdownOption = remember { mutableStateOf(false) }
    val showDeletePopup = remember { mutableStateOf(false) }
    val isDeleteAlbumDropdownOption = remember { mutableStateOf(false) }
    val isRenameAlbumDropdownOption = remember { mutableStateOf(false) }
    var albumName by remember { mutableStateOf("Album name") }

    Scaffold (
        topBar = {
            MyTopAppBar(
                title = "",
                titleLeftButton = "Albums",
                onNavigationClick = { navController.navigate("MyAlbumScreen") },
                onActionClick = {},
                actionIcon = Icons.Default.MoreVert,
                menuItems = listOf(
                    "Select" to {isSelectedDropdownOption.value = true},
                    "Rename" to {isRenameAlbumDropdownOption.value = true},
                    "Delete Album" to {isDeleteAlbumDropdownOption.value = true}

                )
            )
        },

    ) {
        paddingValues ->
        Box (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if (isRenameAlbumDropdownOption.value) {
                    Row (modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = albumName,
                            onValueChange = { albumName = it },
                            textStyle = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .padding(16.dp),
//                            colors = TextFieldDefaults.textFieldColor(
//                                containerColor = Color.Transparent,
//                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
//                                unfocusedIndicatorColor = Color.Transparent
//                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { isRenameAlbumDropdownOption.value = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.Black
                            )
                        ) {
                            Text(text="Save")
                        }
                    }
                } else {
                    Text(
                        text = albumName,
                        modifier = Modifier
                            .padding(16.dp),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                        .padding(end = 16.dp)) {
                    if(isSelectedDropdownOption.value) {
                        Button(
                            onClick = {
                                if (selectedPhotos.size == photos.size) {
                                    selectedPhotos.clear()
                                } else {
                                    selectedPhotos.clear()
                                    selectedPhotos.addAll(photos)
                                }
                            },
                            colors = ButtonDefaults.buttonColors( // Thay đổi màu nền và màu chữ
                                containerColor = MaterialTheme.colorScheme.primary, // Màu nền
                                contentColor = Color.Black // Màu chữ
                            ),
                            modifier = Modifier.padding(start = 16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text ="Select all",
                                )
                            }
                        }

                        Button(
                            onClick = { isSelectedDropdownOption.value = !isSelectedDropdownOption.value },
                            colors = ButtonDefaults.buttonColors( // Thay đổi màu nền và màu chữ
                                containerColor = MaterialTheme.colorScheme.primary, // Màu nền
                                contentColor = Color.Black // Màu chữ
                            ),
                            modifier = Modifier.padding(start = 16.dp)
                        ) {
                            Text(
                                text ="Done",
                            )
                        }
                    }
                    else {
                        Button(
                            onClick = { navController.navigate("SelectImageForAlbum") },
                            colors = ButtonDefaults.buttonColors( // Thay đổi màu nền và màu chữ
                                containerColor = MaterialTheme.colorScheme.primary, // Màu nền
                                contentColor = Color.Black // Màu chữ
                            ),
                            modifier = Modifier.padding(start = 16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                Text( text ="Add photos" )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxHeight(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(40) { index ->
                        val photoId = photos[0]
                        val isSelected = selectedPhotos.contains(photoId)

                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight() // Sử dụng toàn bộ không gian có sẵn
                                .toggleable(
                                    value = isSelected,
                                    onValueChange = {
                                        if (isSelected) {
                                            selectedPhotos.remove(photoId)
                                        } else {
                                            selectedPhotos.add(photoId)
                                        }
                                    }
                                )
                        ) {
                            val screenWidth = maxWidth

                            Image(
                                painter = painterResource(id = photoId),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(screenWidth * 1f)
                            )
                            if (isSelectedDropdownOption.value) {
                                if (isSelected) {
                                    Box (
                                        modifier = Modifier
                                            .matchParentSize()
                                            .background(Color.White.copy(alpha = 0.3f))
                                            .clip(RoundedCornerShape(8.dp))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .border(
                                                    width = 2.dp, // Độ dày của viền
                                                    color = Color.White, // Màu sắc của viền
                                                    shape = CircleShape // Đảm bảo viền là hình tròn
                                                )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Selected",
                                                tint = Color.Blue,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (isSelectedDropdownOption.value) {
            FloatingButtonExample(Icons.Default.Delete, "Delete", showDeletePopup)
        }

        if(isRenameAlbumDropdownOption.value) {

        }

        if(isDeleteAlbumDropdownOption.value) {
            showDeletePopup.value = true;
            if(showDeletePopup.value) {
                Popup(
                    alignment = Alignment.Center,
                    onDismissRequest = { showDeletePopup.value = false }
                ) {
                    Box(
                        modifier = Modifier
                            .width(250.dp)
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.secondary)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Are you sure to delete this album?",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "This album will be removed and you cannot reserve it in the future",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row {
                                Button(
                                    onClick = {
                                        isDeleteAlbumDropdownOption.value = false
                                        showDeletePopup.value = false
                                    }
                                ) {
                                    Text(text = "Cancel")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(onClick = { }) {
                                    Text(
                                        text = "Delete",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FloatingButtonExample(icon: ImageVector, iconName: String, showDeletePopup: MutableState<Boolean> = remember { mutableStateOf(false) }) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Nút nổi cố định phía dưới
        FloatingActionButton(
            onClick = { showDeletePopup.value = true },
            modifier = Modifier
                .align(Alignment.BottomEnd) // Cố định ở góc phải dưới
                .padding( 16.dp ) // Khoảng cách từ cạnh màn hình
                .fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Add"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = iconName)
            }
        }
        if (showDeletePopup.value) {
            Popup(
                alignment = Alignment.Center,
                onDismissRequest = { showDeletePopup.value = false }
            ) {
                Box(
                    modifier = Modifier
                        .width(250.dp)
                        .height(150.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Delete 4 items?",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "These photos will be removed from the album",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row {
                            Button(onClick = { showDeletePopup.value = false }) {
                                Text(text = "Cancel")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = { }) {
                                Text(
                                    text = "Delete",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DisplayPhotoInAlbumPreview() {
    val mockNavController = rememberNavController() // Mock NavController cho Preview
    MyApplicationTheme { // Thêm theme của bạn ở đây
        DisplayPhotoInAlbum(navController = mockNavController)
    }
}
