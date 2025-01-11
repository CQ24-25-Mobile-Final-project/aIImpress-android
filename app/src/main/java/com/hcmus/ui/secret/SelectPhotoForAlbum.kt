package com.hcmus.ui.secret

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.hcmus.data.ContextStore
import com.hcmus.ui.album.ImagePickerScreen



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectPhotoForAlbum(navController: NavController,context: Context,albumModel: AlbumModel) {
    // Lấy email từ ContextStore
    val email = remember { ContextStore.get(context, "email") ?: "" }

    // Nếu email không hợp lệ, không tiếp tục
    if (email.isBlank()) {
        Log.e("SecretPhotoViewScreen", "Email not found in ContextStore")
        return
    }

    val context = LocalContext.current
    ImagePickerScreen(context = context)
    val photos = remember { mutableStateOf<List<Uri>>(fetchImages(context)) }
    val selectedPhotos = albumModel.selectedPhotos.observeAsState(emptyList())
    val albumName = remember { Albums.albumName }

    // In log giá trị albumName
    Log.d("test", "Album Name: $albumName")

    RequestMediaPermissions {
        photos.value = fetchImages(context)
    }
    // Kiểm tra nếu có hình ảnh nào được chọn
    val isAddEnabled = selectedPhotos.value.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "All Items")
                        Text(
                            text = "add ${selectedPhotos.value.size} items to Secret",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                },

                navigationIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                        }
                        Text(text = "Back", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            Log.d("SelectedPhotos", "Selected photos: ${selectedPhotos.value}")
                            Log.d("SelectPhotoForAlbum", "Selected Album Name: $albumName")

                            Log.d("SelectPhotoForAlbum", "Selected Photos: ${selectedPhotos.value}")


                            if (!albumName.isNullOrBlank()) {
                                albumModel.addPhotosToAlbum(
                                    email = email,
                                    albumName = albumName,
                                    photos = selectedPhotos.value
                                )
                                navController.navigate("view")
                            } else {
                                Log.e("SelectPhotoForAlbum", "Album Name is null or blank")
                            }
                        },
                        enabled = isAddEnabled
                    ) {
                        Text(text = "Add")
                    }
                }

            )
        }
    ) { paddingValues ->

        // Nếu không có ảnh, hiển thị thông báo "No Photos or Video"
        if (photos.value.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "No Photos or Video", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4), // Thay đổi thành 4 cột
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding = PaddingValues(2.dp)
            ) {
                items(photos.value) { imageUri ->
                    val isSelected = selectedPhotos.value.contains(imageUri)
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable {
                                val currentSelected = selectedPhotos.value.toMutableList()
                                if (isSelected) currentSelected.remove(imageUri)
                                else currentSelected.add(imageUri)
                                albumModel.updateSelectedPhotos(currentSelected)
                            }
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = imageUri),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(), // Bỏ bo góc
                            contentScale = ContentScale.Crop
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(24.dp)
                                    .background(Color.Blue, shape = CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
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
    // Log the number of images and some URIs for debugging
    Log.d("fetchImages", "Fetched ${images.size} images.")
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
