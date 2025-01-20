package com.hcmus.ui.secret

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.hcmus.R
import com.hcmus.data.ContextStore
import com.hcmus.data.model.Album



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectAlbumToAddPhoto(
    navController: NavController,
    context: Context,
    albumModel: AlbumModel,
    photoUri: String
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
                    Text(text = "Vault")
                },
                navigationIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                        }
                        Text(text = "Back", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.width(32.dp))
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            selectedAlbum.value?.let { album ->

                                if (photoUri.isNotBlank()) {
                                    val uri = Uri.parse(photoUri)
                                    albumModel.markPhotoAsDeleted(uri)
                                    albumModel.addPhotosToAlbum(email, album.name, listOf(uri))
                                    Toast.makeText(context, "Thêm ảnh thành công vào secret ${album.name}", Toast.LENGTH_SHORT).show()
                                    navController.navigate("galleryScreen")
                                } else {
                                    albumModel.selectAlbum(album.name)
                                    navController.navigate("select_photo_for_album")
                                }
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
                    val isSelected = selectedAlbum.value == album // So sánh đúng kiểu Album
                    AlbumItem(
                        albumName = album.name,
                        photoCount = album.images.size,
                        isSelected = isSelected,
                        onClick = {
                            selectedAlbum.value = if (isSelected) null else album
                        }
                    )
                }
            }

            // Nút tạo album mới
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = { navController.navigate("create_new_album") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary),
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text(
                    text = "Create New Album",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }

        }
    }
}

@Composable
fun AlbumItem(
    albumName: String,
    photoCount: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray) // Màu nền mặc định
            .border(
                width = if (isSelected) 2.dp else 0.dp, // Độ dày viền nếu được chọn
                color = if (isSelected) Color.Blue else Color.Transparent, // Màu viền
                shape = RoundedCornerShape(8.dp) // Hình dạng đường viền
            )
            .aspectRatio(1f)
            .clickable { onClick() }
            .padding(3.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Album Icon or Image
            Image(
                painter = painterResource(id = R.drawable.album), // Thay bằng ảnh biểu tượng
                contentDescription = "Album Icon",
                modifier = Modifier
                    .weight(3f) // Tỷ lệ chiều cao cho hình ảnh
                    .fillMaxWidth()
                    .background(Color.White), // Nền màu trắng cho biểu tượng
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(2.dp))
            // Album Info (Row: Name + Photo Count)
            Row(
                modifier = Modifier
                    .weight(1f) // Tỷ lệ chiều cao cho thông tin
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Album Name
                Text(
                    text = if (albumName.length > 12) albumName.take(12) + "..." else albumName, // Rút gọn tên
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(2f),
                    maxLines = 1 // Đảm bảo chỉ hiển thị 1 dòng
                )
                Spacer(modifier = Modifier.width(16.dp))
                // Photo Count
                Text(
                    text = "$photoCount",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}


