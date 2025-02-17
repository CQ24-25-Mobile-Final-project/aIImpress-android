package com.hcmus.ui.secret

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.hcmus.R
import com.hcmus.data.ContextStore
import com.hcmus.data.model.Album
import com.hcmus.ui.theme.BluePrimary


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectAlbumToAddPhoto(
    navController: NavController,
    context: Context,
    albumModel: AlbumModel
) {

    val email = remember { ContextStore.get(context, "email") ?: "" }

    // Nếu email không hợp lệ, không tiếp tục
    if (email.isBlank()) {
        Log.e("SecretPhotoViewScreen", "Email not found in ContextStore")
        return
    }
    // Trạng thái album được chọn
    val selectedAlbum = remember { mutableStateOf<Album?>(null) }
    val albums = albumModel.albums.observeAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        albumModel.fetchAlbums(email) // Thay "user_email@example.com" bằng email thực tế
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {

                },
                navigationIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                painter = painterResource(id = R.drawable.back_icon),
                                contentDescription = "Back",
                                tint = BluePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(32.dp))
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            selectedAlbum.value?.let { album ->
                                albumModel.selectAlbum(album.name) // Lưu album được chọn
                                navController.navigate("select_photo_for_album")
                            }
                        },

                        enabled = selectedAlbum.value != null
                    ) {
                        Text(text = "Done")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(albums.value) { album -> // Đảm bảo albums.value không null
                    var isSelected = selectedAlbum.value == album // So sánh đúng kiểu Album
                    AlbumItemListView(
                        albumName = album.name,
                        photoCount = album.images.size,
                        firstPhotoUri = album.images.getOrNull(0)?.let { Uri.parse(it) },
                        isSelected = isSelected,
                        onClick = { selectedAlbum.value = album }
                    )
                }
            }


        }
    }
}

@Composable
fun AlbumItemListView(albumName: String, photoCount: Int, firstPhotoUri: Uri?, isSelected: Boolean, onClick: () -> Unit) {
    Column (
        modifier = Modifier
        .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) BluePrimary else Color.Transparent,
    )
        .clickable { onClick() }
    ) {
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


