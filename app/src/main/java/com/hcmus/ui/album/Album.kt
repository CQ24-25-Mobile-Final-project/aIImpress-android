package com.hcmus.ui.album

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.hcmus.R
import com.hcmus.ui.components.CustomBottomBar
import com.hcmus.ui.components.GalleryTopBar
import com.hcmus.ui.theme.BluePrimary
import com.hcmus.utils.SmartAlbumOrganizer

var flag = false
var countOfImages = 0

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAlbumScreen(navController: NavController) {
    var expanded by remember { mutableStateOf(false)}
    var isGridView by remember { mutableStateOf(true) }
    val albumViewModel: AlbumViewModel = hiltViewModel()
    val albums by albumViewModel.albums.observeAsState(emptyList())
    var showPopupAddNewAlbum by remember { mutableStateOf(false) }
    var NewAlbumName by remember { mutableStateOf("") }
    val menuItems = listOf("Create Album", "Add Photos")
    val menuIcons = listOf(
        Icons.Filled.Folder,    // Create Folder
        Icons.Filled.Add,       // Add Items

    )
    var showAlertDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val photos = remember { mutableStateOf<List<Uri>>(emptyList()) }
    photos.value = fetchImages(context)
    if (photos.value.size != countOfImages) {
        flag = false
        countOfImages = photos.value.size
    }
    RequestMediaPermissions {
        if (!flag) {
            photos.value = fetchImages(context)

            photos.value.forEach { uri ->
                SmartAlbumOrganizer(uri.toString(), context, albumViewModel)
            }

            flag = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Album") },
                navigationIcon = {
                    IconButton(onClick = {
                        // Điều hướng về màn hình chính và xóa các màn hình trung gian
                        navController.navigate("gallery") {
                            popUpTo("gallery") { inclusive = false }
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.back_icon),
                            contentDescription = "Back",
                            tint = BluePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                actions= {// cái nút ba chấm
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { expanded = !expanded }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        
                        SmallExample(onClick = { showPopupAddNewAlbum = true })
                    }

                    if (showPopupAddNewAlbum) {
                        AlertDialog(
                            onDismissRequest = { showPopupAddNewAlbum = false },
                            title = {
                                Text(
                                    "Create New Album",
                                    style = MaterialTheme.typography.titleLarge
                                )
                            },
                            text = {
                                Column {
                                    TextField(
                                        value = NewAlbumName,
                                        onValueChange = { NewAlbumName = it },
                                        label = { Text(text = "Album name") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        if (NewAlbumName.isNotBlank()) {
                                            showPopupAddNewAlbum = false
                                            albumViewModel.addAlbumName(NewAlbumName)
                                            navController.navigate("SelectImageForAlbum")
                                        }
                                    }
                                ) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showPopupAddNewAlbum = false }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            var selectedIndex = 1
            CustomBottomBar(
                selectedIndex = selectedIndex,
                onTabSelected = { index ->
                    selectedIndex = index
                    if (index == 3) navController.navigate("photo_map")
                    if (index == 0) navController.navigate("gallery")

                },
                onAddClick = {
                    navController.navigate("appContent")
                },
                navController = navController
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                BottomSheetExample(albumViewModel)
                IconButton(onClick = { isGridView = !isGridView }) {
                    Icon(
                        imageVector = if (isGridView) Icons.Default.Check else Icons.Default.CheckCircle,
                        contentDescription = if (isGridView) "Switch to grid view" else "Switch to list view"
                    )
                }
                IconButton(onClick = { showPopupAddNewAlbum = !showPopupAddNewAlbum }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = if (isGridView) "Switch to grid view" else "Switch to list view"
                    )
                }
            }

            if (isGridView) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.wrapContentHeight()
                ) {
                    items(albums) { (name, photos) ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                                .clickable(
                                    onClick = {
                                        try {
                                            albumViewModel.selectAlbum(name)
                                            navController.navigate("DisplayPhotoInAlbum")
                                        } catch (e: Exception) {
                                            Log.e("Navigation Error", e.message.toString())
                                        }
                                    }
                                )
                        ) {
                            AlbumItemGridView(name, photos.size, photos.getOrNull(0))
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(0.dp, 8.dp, 4.dp, 8.dp)
                ) {
                    items(albums) { (name, photos) ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                                .clickable(
                                    onClick = {
                                        try {
                                            albumViewModel.selectAlbum(name)
                                            navController.navigate("DisplayPhotoInAlbum")
                                        } catch (e: Exception) {
                                            Log.e("Navigation Error", e.message.toString())
                                        }
                                    }
                                )
                        ) {
                            AlbumItemListView(name, photos.size, photos.getOrNull(0))
                        }
                    }
                }
            }
        }
    }

    if (showPopupAddNewAlbum) {
        AlertDialog(
            onDismissRequest = { showPopupAddNewAlbum = false },
            title = {
                Text("Create New Album", style = MaterialTheme.typography.titleLarge)
            },
            text = {
                Column {
                    TextField(
                        value = NewAlbumName,
                        onValueChange = { NewAlbumName = it },
                        label = { Text(text="Album name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (NewAlbumName.isNotBlank()) {
                            showPopupAddNewAlbum = false
                            albumViewModel.addAlbumName(NewAlbumName)
                            navController.navigate("SelectImageForAlbum")
                        }
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPopupAddNewAlbum = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AlbumItemListView(albumName: String, photoCount: Int, firstPhotoUri: Uri?) {
    Row (verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
            .padding(0.dp, 0.dp, 8.dp, 4.dp)) {
        Image(
            painter = if (firstPhotoUri != null) {
                rememberAsyncImagePainter(model = firstPhotoUri)
            } else {
                painterResource(id = R.drawable.avatar)
            },
            contentDescription = "wallpaper of an item",
            modifier = Modifier
                .size(100.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = albumName,
                style = MaterialTheme.typography.titleMedium)
            Text(text = photoCount.toString() + if(photoCount == 1 || photoCount == 0)  " photo" else " photos",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AlbumItemGridView(albumName: String, photoCount: Int, firstPhotoUri: Uri?) {
    Column {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight()
        ) {
            Image(
                painter = if (firstPhotoUri != null) {
                    rememberAsyncImagePainter(model = firstPhotoUri)
                } else {
                    painterResource(id = R.drawable.avatar)
                },
                contentDescription = null,
                modifier = Modifier
                    .size(maxWidth * 1f)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = albumName,
                style = MaterialTheme.typography.titleMedium)

            Text(text = photoCount.toString() + if(photoCount == 1 || photoCount == 0)  " photo" else " photos",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun BottomSheetExample(albumViewModel: AlbumViewModel) {
    var showPopup by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Sort") }

    Button(
        onClick = { showPopup = true },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = Color.Black // Màu chữ
        ),
        modifier = Modifier.padding(8.dp)
    ) {
        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
        Text(selectedOption)
    }

    if (showPopup) {
        AlertDialog(
            onDismissRequest = { showPopup = false },
            title = {
                Text("Sort by", style = MaterialTheme.typography.titleLarge)
            },
            text = {
                Column {
                    RadioButtonOption(
                        text = "Last Modified",
                        selectedOption = selectedOption,
                        onSelect = {
                            selectedOption = "Last Modified"
                            showPopup = false
                        }
                    )
                    RadioButtonOption(
                        text = "Most photos",
                        selectedOption = selectedOption,
                        onSelect = {
                            selectedOption = "Most photos"
                            showPopup = false
                            albumViewModel.sortAlbumsByPhotoCount()
                        }
                    )
                    RadioButtonOption(
                        text = "Album name",
                        selectedOption = selectedOption,
                        onSelect = {
                            selectedOption = "Album name"
                            showPopup = false
                            albumViewModel.sortAlbumsByName()
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPopup = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun RadioButtonOption(text: String, selectedOption: String, onSelect: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selectedOption == text,
            onClick = onSelect
        )
        Text(text = text, modifier = Modifier.padding(start = 8.dp))
    }
}
@Composable
fun SmallExample(onClick: () -> Unit) {
    SmallFloatingActionButton(
        onClick = { onClick() },
        modifier = Modifier.size(60.dp)
    ) {
        Icon(Icons.Filled.Add, "Small floating action button.", tint = BluePrimary)
    }
}
