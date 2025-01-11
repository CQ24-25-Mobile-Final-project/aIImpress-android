package com.hcmus.ui.Trash
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import com.hcmus.ui.secret.AlbumModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.hcmus.R

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewTrashAlbumScreen() {
    // Mock NavController và AlbumModel cho preview
    val navController = rememberNavController()
    TrashAlbumScreen(navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashAlbumScreen(navController: NavController) {
    // Tạo danh sách ảnh với số ngày ngẫu nhiên
    val photos = List(14) { Pair(R.drawable.avatar, (1..30).random()) }
    val videos = List(2) { Pair(R.drawable.avatar, (1..30).random()) }

    // Kết hợp và sắp xếp theo số ngày từ lớn đến nhỏ
    val allMedia = (photos + videos).sortedByDescending { it.second }

    // Quản lý trạng thái chọn
    var isSelecting by remember { mutableStateOf(false) }
    val (selectedItems, setSelectedItems) = remember { mutableStateOf(setOf<Pair<Int, Int>>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (!isSelecting) { // Chỉ hiển thị tiêu đề khi không ở trạng thái chọn
                        Text(text = "Đã xóa gần đây")
                    } else {
                        Spacer(modifier = Modifier) // Thay thế bằng khoảng trống
                    }
                },
                navigationIcon = {
                    if (!isSelecting) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                        }
                    } else {
                        Spacer(modifier = Modifier)
                    }
                },
                actions = {
                    TextButton(onClick = { isSelecting = !isSelecting }) {
                        Text(
                            text = if (isSelecting) "Hủy" else "Chọn",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (isSelecting) { // Hiển thị BottomAppBar khi đang chọn
                BottomAppBar(
                    containerColor = Color.White,
                    content = {
                        TextButton(
                            onClick = { /* Logic chọn mục */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Chọn Mục", style = MaterialTheme.typography.bodyLarge)
                        }
                        IconButton(
                            onClick = { /* Logic icon ba chấm */ },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.blue_dots_icon), // Thay icon bằng R.drawable.blue_dots_icon
                                contentDescription = "More Options"
                            )
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding = PaddingValues(2.dp)
            ) {
                items(allMedia) { media ->
                    val isSelected = selectedItems.contains(media)
                    TrashImageDrawableItem(
                        drawableRes = media.first,
                        daysLeft = media.second,
                        isSelected = isSelected,
                        onClick = {
                            setSelectedItems(
                                if (isSelected) selectedItems - media else selectedItems + media
                            )
                        }
                    )
                }
            }

            if (!isSelecting) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${photos.size} ảnh, ${videos.size} video",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.padding(top = 16.dp))
                        Text(
                            text = "Các ảnh và video hiển thị số ngày còn lại trước khi bị xóa. Sau thời gian đó, các mục sẽ bị xóa vĩnh viễn. Thời gian này có thể kéo dài đến 30 ngày.",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun TrashImageDrawableItem(
    @DrawableRes drawableRes: Int,
    daysLeft: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color.Blue.copy(alpha = 0.5f) else Color.LightGray)
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = drawableRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(4.dp))
                .padding(4.dp)
        ) {
            Text(
                text = "$daysLeft ngày",
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
