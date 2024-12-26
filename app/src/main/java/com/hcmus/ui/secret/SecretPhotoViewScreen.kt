package com.hcmus.ui.secret

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation.NavController
import com.hcmus.R
import androidx.compose.foundation.gestures.detectTapGestures

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SecretPhotoViewScreen(navController: NavController, onBackPressed: () -> Unit) {
    // danh sách các album
    var expanded by remember { mutableStateOf(false)}// kiểm soát trạng thái đóng mở của menu
    var showMenu by remember { mutableStateOf(false) }
    // câph nhật các biến trạng thái
    var showAlertDialog by remember { mutableStateOf(false) } // Trạng thái của AlertDialog
    var showLongPressDialog by remember { mutableStateOf(false) }
    var selectedAlbum by remember { mutableStateOf<Pair<String, List<Any>>?>(null) }
    var renameAlbumInput by remember { mutableStateOf("") } // Biến lưu tên mới nhập vào
    var showAlertDialogRename by remember{ mutableStateOf(false)}

    var albumNameInput by remember { mutableStateOf("") } // Biến lưu tên album nhập từ người dùng

    val menuItems = listOf("Create Folder", "Add Items", "Ascending(A-Z)", "Descending(Z-A)")
    val menuIcons = listOf(
        Icons.Filled.Folder,    // Create Folder
        Icons.Filled.Add,       // Add Items
        Icons.Filled.ArrowUpward, // Ascending (A-Z)
        Icons.Filled.ArrowDownward // Descending (Z-A)
    )

    val fabMenuItems = listOf("New Album", "Import Photos")
    val fabMenuIcons = listOf(
        Icons.Filled.Folder, // Icon cho New Album
        Icons.Filled.Image // Icon cho Import Photos
    )
    var isGridView by remember { mutableStateOf(true) }


    val albums by remember { derivedStateOf { Albums.albums } }

    LaunchedEffect(Unit) {
        if (Albums.albums.isEmpty()) {
            Albums.addAlbum("DefaultVault", emptyList()) // Tạo album mặc định nếu cần
        }
    }

    val Purple40 = Color(0xFF1B87C9)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gallery Vault") },
                navigationIcon = {
                    IconButton(onClick = {
                        // Điều hướng về màn hình chính và xóa các màn hình trung gian
                        navController.navigate("gallery") {
                            popUpTo("gallery") { inclusive = false }
                        }
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back",tint=Color.Black)
                    }
                },
                actions={// cái nút ba chấm
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { expanded = !expanded }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "More Options")
                    }

                    // DropdownMenu sẽ được hiển thị bên dưới nút ba chấm
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },// ấn vào bên ngoài hay trong menu thì đóng lại
                        modifier = Modifier
                            .offset { IntOffset(0, 5) } // Di chuyển menu xuống dưới nút ba chấm
                            .width(250.dp)
                    ) {
                        menuItems.forEachIndexed { index, item ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Hiển thị Icon cho mỗi mục

                                        Text(
                                            item,
                                            modifier = Modifier
                                                .padding(vertical = 8.dp)
                                                .weight(1f),
                                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 22.sp)
                                        )

                                        Icon(
                                            imageVector = menuIcons[index],
                                            contentDescription = item,
                                            modifier = Modifier.padding(end = 8.dp) // Khoảng cách giữa icon và text
                                        )
                                    }
                                },
                                modifier = Modifier.size(250.dp, 48.dp),
                                onClick = {
                                    expanded = false
                                    if (item == "Create Folder") {
                                        showAlertDialog = true
                                    }else if(item =="Add Items"){
                                        navController.navigate("select_photo_for_album")
                                    }
                                }

                            )
                            // Nếu không phải mục cuối, thêm một đường phân cách (divider)
                            if (index < menuItems.size - 1) {
                                Divider(color = Color.LightGray , thickness = 1.dp)
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton={
            FloatingActionButton(onClick = {
                showMenu = !showMenu // Chuyển đổi trạng thái hiển thị menu
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Create Album")
            }
        },

        content = { paddingValues ->  // Thêm padding vào content
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
                    IconButton(onClick = { isGridView = !isGridView }) {
                        Icon(

                            imageVector = if (isGridView) Icons.Default.Check else Icons.Default.CheckCircle,
                            contentDescription = if (isGridView) "Switch to grid view" else "Switch to list view",
                            tint=Color.Black
                        )
                    }
                }

                // Hiển thị danh sách album
                if (isGridView) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.wrapContentHeight()
                    ) {
                        items(albums){ album ->
                            Box(
                                modifier = Modifier
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onLongPress = {
                                                selectedAlbum = album // Lưu album được long press
                                                showLongPressDialog = true // Hiển thị AlertDialog
                                            },
                                            onTap = {
                                                navController.navigate("display_photo_in_album/${album.first}")
                                            }
                                        )
                                    }
                                    .fillMaxSize()
                                    .padding(8.dp)
                            ) {
                                AlbumItemGridView(
                                    albumName = album.first,
                                    photoCount = album.second.size,
                                ) // Chỉnh sửa để thêm số lượng ảnh thực tế
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(0.dp, 8.dp, 4.dp, 8.dp)
                    ) {
                        items(albums) { album ->
                            Box(
                                modifier = Modifier
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onLongPress = {
                                                selectedAlbum = album // Lưu album được long press
                                                showLongPressDialog = true // Hiển thị AlertDialog
                                            },
                                            onTap = {
                                                navController.navigate("display_photo_in_album/${album.first}")
                                            }
                                        )
                                    }
                                    .fillMaxSize()
                                    .padding(8.dp)
                            ) {
                                AlbumItemListView(
                                    albumName = album.first,
                                    photoCount = album.second.size,
                                ) // Chỉnh sửa để thêm số lượng ảnh thực tế
                            }
                        }
                    }
                }
            }

        }

    )
    // Menu cho nút dấu cộng (Floating Action Button)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // Padding toàn màn hình để đảm bảo không che khuất nút
            .wrapContentSize(Alignment.BottomEnd) // Căn chỉnh góc dưới phải cho box
    ) {
        DropdownMenu(
            expanded = showMenu, // Sử dụng showMenu cho nút dấu cộng
            onDismissRequest = { showMenu = false }, // Đóng menu khi nhấn ngoài
            modifier = Modifier
                .offset { IntOffset(0, -10) } // Di chuyển menu lên trên
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
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 22.sp)
                            )

                            Icon(
                                imageVector = fabMenuIcons[index],
                                contentDescription = item,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(30.dp)
                            )
                        }
                    },
                    onClick = {
                        showMenu = false

                        if (item == "New Album") {
                            showAlertDialog = true
                        }else if(item =="Import Photos"){
                            navController.navigate("select_photo_for_album")
                        }
                    }
                )
                if (index < fabMenuItems.size - 1) {
                    Divider(color = Color.LightGray, thickness = 1.dp)
                }
            }
        }
    }
    // Hiển thị AlertDialog khi showAlertDialog là true
    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = { showAlertDialog = false },
            title = { Text(text ="New Folder") },
            text = {
                Column{
                    TextField(
                        value = albumNameInput,
                        onValueChange = {albumNameInput=it},
                        placeholder = {
                            Text(
                                text = "Folder Name",
                                color = Color.Gray// Màu chữ placeholder khi chưa nhập
                            )
                        },
                        modifier= Modifier.fillMaxWidth(),

                        )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if(albumNameInput.isNotBlank()){// cái tên không rỗng
                            Albums.addAlbum(albumNameInput, emptyList())
                            albumNameInput=""
                            showAlertDialog = false
                        }else{

                        }


                    }
                ) {
                    Text("Create",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                    )

                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        albumNameInput=""
                        showAlertDialog = false
                    }
                ) {
                    Text("Cancel",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                    )

                }
            },
            containerColor = Color.LightGray

        )
    }else if (showLongPressDialog) {
        val isDefaultAlbum = selectedAlbum?.first == "DefaultVault" // Kiểm tra album có phải default không
        AlertDialog(
            onDismissRequest = { showLongPressDialog = false },
            title = { Text("Album Options") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally // Căn giữa nội dung
                ) {
                    Text(
                        text = "Add Items",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("select_photo_for_album") // Điều hướng đến thêm ảnh
                                showLongPressDialog = false // Đóng dialog
                            }
                            .padding(8.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    // Chỉ hiển thị Rename và Delete nếu không phải album mặc định
                    if (!isDefaultAlbum) {
                        Text(

                            text = "Rename",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp,),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    renameAlbumInput = selectedAlbum?.first.orEmpty() // Gán tên album hiện tại
                                    showLongPressDialog = false // Đóng dialog hiện tại
                                    showAlertDialogRename = true // Hiển thị dialog đổi tên
                                }
                                .padding(8.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        Text(
                            text = "Delete",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // Xử lý delete album
                                    Albums.deleteAlbum(selectedAlbum?.first.orEmpty())
                                    showLongPressDialog = false // Đóng dialog
                                }
                                .padding(8.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLongPressDialog = false }) {
                    Text("Close")
                }
            },
            containerColor = Color.LightGray
        )
    }else if (showAlertDialogRename) {
        AlertDialog(
            onDismissRequest = { showAlertDialogRename = false },
            title = { Text("Rename Album") },
            text = {
                Column {
                    TextField(
                        value = renameAlbumInput,
                        onValueChange = { renameAlbumInput = it },
                        placeholder = {
                            Text(
                                text = "New Album Name",
                                color = Color.Gray
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Kiểm tra tên mới hợp lệ và thực hiện đổi tên
                        if (renameAlbumInput.isNotBlank() && selectedAlbum != null) {
                            // Gọi phương thức đổi tên album
                            Albums.renameAlbum(selectedAlbum!!.first, renameAlbumInput)

                            // Cập nhật tên album trong danh sách
                            selectedAlbum = selectedAlbum?.copy(first = renameAlbumInput)

                            // Reset input và album sau khi đổi tên
                            renameAlbumInput = "" // Reset input sau khi đổi tên
                            showAlertDialogRename = false // Đóng dialog
                        }
                    }
                ) {
                    Text("Rename", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        renameAlbumInput = "" // Reset input nếu hủy
                        showAlertDialogRename = false
                    }
                ) {
                    Text("Cancel", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp))
                }
            },
            containerColor = Color.LightGray
        )
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

@Composable
fun AlbumItemGridView(albumName: String, photoCount: Int) {
    Column {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight()
        ) {
            val screenWidth = maxWidth

            Image(
                painter = painterResource(id = R.drawable.wallpaper),
                contentDescription = null,
                modifier = Modifier
                    .size(screenWidth * 1f)
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


