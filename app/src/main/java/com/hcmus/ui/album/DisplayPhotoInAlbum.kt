package com.hcmus.ui.album

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.hcmus.ui.theme.MyApplicationTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.hcmus.ui.components.GalleryTopBar
import com.hcmus.ui.components.MyTopAppBar
import androidx.compose.runtime.livedata.observeAsState

@Composable
fun DisplayPhotoInAlbum(navController: NavController) {
    val albumViewModel: AlbumViewModel = hiltViewModel()
    val selectedPhotos = remember { mutableStateListOf<Uri>() }
    val isSelectedDropdownOption = remember { mutableStateOf(false) }
    val showDeletePopup = remember { mutableStateOf(false) }
    val isDeleteAlbumDropdownOption = remember { mutableStateOf(false) }
    val isRenameAlbumDropdownOption = remember { mutableStateOf(false) }
    val albumName = remember { AlbumRepository.albumName }
    val photos by albumViewModel.photos.observeAsState(emptyList())

    LaunchedEffect(albumName) {
        albumViewModel.selectAlbum(albumName)
    }
    Scaffold (
        topBar = {
            GalleryTopBar(navController)
            MyTopAppBar(
                title = "$albumName",
                titleLeftButton = "",
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
                val newAlbumName = albumName
                if (isRenameAlbumDropdownOption.value) {
                    Row (modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = newAlbumName,
                            onValueChange = { albumViewModel.addAlbumName(newAlbumName) },
                            textStyle = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .padding(16.dp),
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
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.padding(start = 16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text ="Select all",
                                    color = Color.White
                                )
                            }
                        }

                        Button(
                            onClick = {
                                isSelectedDropdownOption.value = !isSelectedDropdownOption.value
                                selectedPhotos.clear()
                                      },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.padding(start = 16.dp)
                        ) {
                            Text(
                                text ="Done",
                                color = Color.White
                            )
                        }
                    }
                    else {
                        Button(
                            onClick = { navController.navigate("SelectImageForAlbum") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.padding(start = 5.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                Text( text ="Add photos" ,
                                    color = Color.White
                                )

                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(photos) { imageUri ->
                        val isSelected = selectedPhotos.contains(imageUri)

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clickable {
                                    if(!isSelectedDropdownOption.value)
                                    {
                                        navController.navigate("imageDetail/${Uri.encode(imageUri.toString())}")
                                    }
                                    else {
                                        if (isSelected) selectedPhotos.remove(imageUri)
                                        else selectedPhotos.add(imageUri)
                                    }
                                }
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(model = imageUri),
                                contentDescription = "Loaded image: $imageUri",
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.Crop
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(Color.Black.copy(alpha = 0.3f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .border(
                                                width = 2.dp,
                                                color = Color.White,
                                                shape = CircleShape
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
        if (isSelectedDropdownOption.value) {
            FloatingButtonExample(Icons.Default.Delete, "Delete", showDeletePopup, albumName, selectedPhotos, albumViewModel)
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
                            .width(300.dp)
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
                                    Text(text = "Cancel",
                                        color = Color.White)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(onClick = {
                                    AlbumRepository.deleteAlbum(albumName)
                                    isDeleteAlbumDropdownOption.value = false
                                    showDeletePopup.value = false
                                    navController.navigate("MyAlbumScreen")
                                }) {
                                    Text(
                                        text = "Delete",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
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
fun FloatingButtonExample(icon: ImageVector,
                          iconName: String,
                          showDeletePopup: MutableState<Boolean> = remember { mutableStateOf(false)},
                          albumName: String,
                          selectedPhotos: List<Uri>,
                          albumViewModel: AlbumViewModel
                          ) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        FloatingActionButton(
            onClick = { showDeletePopup.value = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding( 16.dp )
                .fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
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
                                Text(text = "Cancel",
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = { selectedPhotos.forEach { photoUri ->
                                albumViewModel.deletePhotoInAlbum(albumName, photoUri)
                            }
                                showDeletePopup.value = false
                            }) {
                                Text(
                                    text = "Delete",
                                    fontWeight = FontWeight.Bold,
                                    color =Color.White
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
    val mockNavController = rememberNavController()
    MyApplicationTheme {
        DisplayPhotoInAlbum(navController = mockNavController)
    }
}
