package com.hcmus.ui.album

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hcmus.ui.components.MyTopAppBar
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.hcmus.R

@Composable
fun MyAlbumScreen (navController: NavController) {
    var isGridView by remember { mutableStateOf(true) }
    val albums = AlbumRepository.albums

    // Log albums value whenever it changes
    LaunchedEffect(albums) {
        Log.d("AlbumsLog", "Albums content: $albums")
    }
    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "Albums",
                titleLeftButton = "Back",
                onNavigationClick = { /* Handle navigation click */ },
                onActionClick = {
                    try {
                        navController.navigate("AddNewAlbum")
                    } catch (e: Exception) {
                        Log.e("Navigation Error", e.message.toString())
                    }
                },
                actionIcon = Icons.Default.Add,
                menuItems = listOf()
            )
        }
    ) {
            paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                BottomSheetExample()
                IconButton (onClick = {isGridView = !isGridView}) {
                    Icon(
                        imageVector = if (isGridView) Icons.Default.Check else Icons.Default.CheckCircle,
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
                        Box (
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                                .clickable(
                                    onClick = {
                                        try {
                                            AlbumRepository.addAlbumName(name)
                                            navController.navigate("DisplayPhotoInAlbum")
                                        } catch (e: Exception) {
                                            Log.e("Navigation Error", e.message.toString())
                                        }
                                    }
                                )
                        ) {
                            AlbumItemGridView(name, photos.size)
                        }
                    }
                }
            }
            else {
                LazyColumn (
                    modifier = Modifier.padding(0.dp, 8.dp, 4.dp, 8.dp)
                ) {
                    items(albums) { (name, photo) ->
                        Box (
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                                .clickable(
                                    onClick = {
                                        try {
                                            navController.navigate("DisplayPhotoInAlbum")
                                        } catch (e: Exception) {
                                            Log.e("Navigation Error", e.message.toString())
                                        }
                                    }
                                )
                        ) {
                            AlbumItemListView(name, photo.size)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlbumItemListView(albumName: String, photoCount: Int) {
    Row (verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
            .padding(0.dp, 0.dp, 8.dp, 4.dp)) {
        Image(
            painter = painterResource(id = R.drawable.wallpaper),
            contentDescription = "wallpaper of an item",
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(12.dp))
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
            Text(text = photoCount.toString() + if(photoCount == 1)  " photo" else " photos",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AlbumItemGridView(albumName: String, photoCount: Int) {
    Column {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight()
        ) {
            Image(
                painter = painterResource(id = R.drawable.wallpaper),
                contentDescription = null,
                modifier = Modifier
                    .size(maxWidth * 1f)
                    .clip(RoundedCornerShape(16.dp))
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

            Text(text = photoCount.toString() + if(photoCount == 1)  " photo" else " photos",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetExample() {
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Sort") }

    Button(
        onClick = { showBottomSheet = true },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = Color.Black // Màu chữ
        ),
        modifier = Modifier.padding(8.dp)
    ) {
        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
        Text(selectedOption)
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Sort by",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Divider()

                CustomTransparentButton(
                    text = "Last Modified",
                    onClick = {
                        selectedOption = "Last Modified"
                        showBottomSheet = false
                    }
                )
                CustomTransparentButton(
                    text = "Most photos",
                    onClick = {
                        selectedOption = "Most photos"
                        showBottomSheet = false
                    }
                )
                CustomTransparentButton(
                    text = "Album name",
                    onClick = {
                        selectedOption = "Album name"
                        showBottomSheet = false
                    }
                )
            }
        }
    }
}

@Composable
fun CustomTransparentButton(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(Color.Transparent)
            .padding(vertical = 12.dp),
        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
    )
}


@Preview(showBackground = true)
@Composable
fun MyAlbumScreenPreview() {
    val mockNavController = rememberNavController()
    MyAlbumScreen(navController = mockNavController)
}