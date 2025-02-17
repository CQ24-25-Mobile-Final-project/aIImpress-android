package com.hcmus.ui.album

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.gson.Gson
import com.hcmus.ui.components.GalleryTopBar
import com.hcmus.ui.components.MyTopAppBar

@Composable
fun SelectImageForAlbum(navController: NavController) {
    val albumViewModel: AlbumViewModel = hiltViewModel()
    val context = LocalContext.current
    ImagePickerScreen(context = context)
    val photos = remember { mutableStateOf<List<Uri>>(emptyList()) }
    val selectedPhotos = remember { mutableStateListOf<Uri>() }
    val albumName = remember { AlbumRepository.albumName }

    RequestMediaPermissions {
        photos.value = fetchImages(context) 
    }

    Scaffold(
        topBar = {
            GalleryTopBar(navController)
            MyTopAppBar(
                title = "Add photos",
                titleLeftButton = "Cancel",
                onNavigationClick = { navController.popBackStack() },
                onActionClick = {
                    if (albumViewModel.albums.value?.find { it.first == albumName } == null) {
                        albumViewModel.addAlbum(albumName, selectedPhotos)
                    } else {
                        albumViewModel.insertIntoAlbum(albumName, selectedPhotos)
                    }
                    navController.navigate("MyAlbumScreen")
                },
                actionIcon = Icons.Default.Done,
                menuItems = listOf()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(photos.value) { imageUri ->
                    val isSelected = selectedPhotos.contains(imageUri)

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .toggleable(
                                value = isSelected,
                                onValueChange = {
                                    if (isSelected) selectedPhotos.remove(imageUri)
                                    else selectedPhotos.add(imageUri)
                                }
                            )
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
}

@Preview(showBackground = true)
@Composable
fun SelectImageForAlbumPreview() {
    val mockNavController = rememberNavController()
    SelectImageForAlbum(
        navController = mockNavController
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestMediaPermissions(content: @Composable () -> Unit) {
    val permissionState = rememberPermissionState(
        permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    )

    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }

    if (permissionState.status.isGranted) {
        // Render the UI when permission is granted
        content()
    } else {
        // Render a placeholder or an error message when permission is denied
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Permission required to access media.")
        }
    }
}

fun fetchImages(context: Context): List<Uri> {
    val images = mutableListOf<Uri>()
    val projection = arrayOf(MediaStore.Images.Media._ID)
    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

    val queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    context.contentResolver.query(queryUri, projection, null, null, sortOrder)?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val uri = ContentUris.withAppendedId(queryUri, id)
            images.add(uri)
        }
    }
    images.take(5).forEach { Log.d("fetchImages", "Image URI: $it") }
    return images
}

@Composable
fun ImageGrid(images: List<Uri>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(images) { imageUri ->
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = null,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}
