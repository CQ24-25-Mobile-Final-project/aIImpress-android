package com.hcmus.ui.Trash


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDateTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashAlbumScreen(navController: NavController, viewModel: TrashViewModel) {
    val trashList by viewModel.trashList.observeAsState(emptyList())
    val validTrashItems = remember(trashList) { trashList.filter { !it.isExpired() } }

    var isSelecting by remember { mutableStateOf(false) }
    val selectedItems = remember { mutableStateOf(mutableSetOf<String>()) }
    var showMenu by remember { mutableStateOf(false) } // Trạng thái hiển thị menu

    val fabMenuItems = listOf("Khôi phục tất cả", "Xóa tất cả")
    val fabMenuIcons = listOf(
        Icons.Filled.Restore,  // Icon khôi phục
        Icons.Filled.Delete   // Icon thùng rác
    )

    val context = LocalContext.current
    val email = remember { FirebaseAuth.getInstance().currentUser?.email ?: "" }

    // Nếu email không hợp lệ, không tiếp tục
    if (email.isBlank()) {
        Log.e("SecretPhotoViewScreen", "Email not found in ContextStore")
        return
    }

    // Đếm số lượng ảnh thực tế từ danh sách Trash
    val totalImages = trashList.sumOf { it.images.size }

    LaunchedEffect(Unit) {
        // Load dữ liệu từ Firestore khi mở màn hình
        viewModel.loadTrashFromFirebase(email )
    }
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
                    if (validTrashItems.isNotEmpty()) { // Chỉ hiển thị nút "Chọn" khi danh sách ảnh không rỗng
                        TextButton(onClick = { isSelecting = !isSelecting }) {
                            Text(
                                text = if (isSelecting) "Hủy" else "Chọn",
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (isSelecting) { // Hiển thị BottomAppBar khi đang chọn
                BottomAppBar(
                    content = {
                        TextButton(
                            onClick = { /* Logic chọn mục */ },
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = if (selectedItems.value.isNotEmpty()) {
                                    "Đã chọn ${selectedItems.value.size} ảnh"
                                } else {
                                    "Chọn mục"
                                },
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp)
                            )
                        }

                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            MoreHorizIconWithCircle()
                        }
                        // Menu cho nút dấu cộng (Floating Action Button)

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
            if (validTrashItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Không có ảnh hoặc video trong thùng rác.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Đảm bảo LazyVerticalGrid chỉ chiếm khoảng không gian cần thiết
                    contentPadding = PaddingValues(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(validTrashItems.flatMap { it.images }) { imageUri ->
                        val isSelected = selectedItems.value.contains(imageUri)
                        val daysLeft = validTrashItems.firstOrNull()?.let { trash ->
                            kotlin.math.max(
                                0,
                                java.time.Duration.between(
                                    LocalDateTime.now(),
                                    trash.getExpiresAtAsLocalDateTime()
                                ).toDays().toInt()
                            )
                        } ?: 0

                        TrashImageDrawableItem(
                            imageUri = imageUri,
                            isSelected = isSelected,
                            isSelectable = isSelecting,
                            daysLeft = daysLeft,
                            onClick = {
                                if (isSelecting) {
                                    selectedItems.value = selectedItems.value.toMutableSet().apply {
                                        if (isSelected) remove(imageUri) else add(imageUri)
                                    }
                                }
                            }
                        )
                    }
                }
                //box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val totalImages =
                            trashList.sumOf { it.images.size } // Tổng số ảnh trong tất cả các mục trash
                        Text(
                            text = "$totalImages ảnh",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.padding(top = 8.dp))
                        Text(
                            text = "Các ảnh và video hiển thị số ngày còn lại trước khi bị xóa. Sau thời gian đó, các mục sẽ bị xóa vĩnh viễn. Thời gian này có thể kéo dài đến 30 ngày.",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                }
            }


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.BottomEnd) // Căn chỉnh góc dưới phải cho box
            ) {
                DropdownMenu(
                    expanded = showMenu, // Sử dụng showMenu cho nút dấu cộng
                    onDismissRequest = { showMenu = false }, // Đóng menu khi nhấn ngoài
                    modifier = Modifier
                        .width(250.dp)
                ) {
                    fabMenuItems.forEachIndexed { index, item ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        item,
                                        modifier = Modifier
                                            .padding(vertical = 8.dp)
                                            .weight(1f),
                                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                                        color = if (index == 1) Color.Red else Color.Black
                                    )

                                    Icon(
                                        imageVector = fabMenuIcons[index],
                                        contentDescription = item,
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .size(30.dp),
                                        tint = if (index == 1) Color.Red else Color.Unspecified

                                    )
                                }
                            },
                            onClick = {

                            }
                        )
                        if (index < fabMenuItems.size - 1) {
                            Divider(color = Color.LightGray, thickness = 1.dp)
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun TrashImageDrawableItem(
    imageUri: String,
    isSelected: Boolean,
    isSelectable: Boolean,
    daysLeft: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = imageUri,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
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
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.6f))
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


@Composable
fun MoreHorizIconWithCircle() {
    // Kích thước và màu sắc có thể tùy chỉnh
    val circleColor = MaterialTheme.colorScheme.primary
    val iconColor = Color.White         // Màu trắng cho icon
    val circleSize = 48.dp              // Kích thước vòng tròn

    Box(
        modifier = Modifier
            .size(circleSize)

            .background( color = circleColor,shape = CircleShape), // Vòng tròn
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.MoreHoriz, // Icon ba chấm ngang
            contentDescription = "More Options",
            tint = iconColor,                    // Màu icon
            modifier = Modifier.size(36.dp)      // Kích thước icon
        )

    }
}

