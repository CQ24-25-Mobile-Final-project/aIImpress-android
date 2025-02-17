package com.hcmus.ui.secret

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.hcmus.data.ContextStore
import com.hcmus.data.model.Album
import com.hcmus.ui.theme.BluePrimary
import com.hcmus.ui.theme.BlueSecondary


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SecretPhotoViewScreen(navController: NavController, context: Context, onBackPressed: () -> Unit,) {
    // danh sách các album
    var expanded by remember { mutableStateOf(false)}// kiểm soát trạng thái đóng mở của menu
    var showMenu by remember { mutableStateOf(false) }
    // câph nhật các biến trạng thái
    var showAlertDialog by remember { mutableStateOf(false) } // Trạng thái của AlertDialog
    var showLongPressDialog by remember { mutableStateOf(false) }
    var selectedAlbum by remember { mutableStateOf<Album?>(null) }
    var renameAlbumInput by remember { mutableStateOf("") } // Biến lưu tên mới nhập vào
    var showAlertDialogRename by remember{ mutableStateOf(false)}

    var albumNameInput by remember { mutableStateOf("") } // Biến lưu tên album nhập từ người dùng

    val menuItems = listOf("Create Folder", "Add Items")
    val menuIcons = listOf(
        Icons.Filled.Folder,    // Create Folder
        Icons.Filled.Add,       // Add Items

    )

    val fabMenuItems = listOf("New Album", "Import Photos")
    val fabMenuIcons = listOf(
        Icons.Filled.Folder, // Icon cho New Album
        Icons.Filled.Image // Icon cho Import Photos
    )
    var isGridView by remember { mutableStateOf(true) }

    // Lấy email từ ContextStore
    val email = remember { ContextStore.get(context, "email") ?: "" }

    // Nếu email không hợp lệ, không tiếp tục
    if (email.isBlank()) {
        Log.e("SecretPhotoViewScreen", "Email not found in ContextStore")
        return
    }

    val albumModel: AlbumModel = hiltViewModel()
    // Quan sát albums
    val albums by albumModel.albums.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        albumModel.fetchAlbums(email)
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Secret Album") },
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
                                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp)
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
                                        navController.navigate("select_album_to_add_photo")
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


        content = { paddingValues ->  // Thêm padding vào content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(3.dp)
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
                                                navController.navigate("display_photo_in_album/${album.name}")
                                            }
                                        )
                                    }
                                    .fillMaxSize()
                                    .padding(8.dp)
                            ) {
                                AlbumItemGridView(
                                    albumName = album.name,
                                    photoCount = album.images.size,
                                    firstPhotoUri = album.images.getOrNull(0)?.let { Uri.parse(it) }
                                )
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
                                                navController.navigate("display_photo_in_album/${album.name}")
                                            }
                                        )
                                    }
                                    .fillMaxSize()
                                    .padding(8.dp)
                            ) {
                                AlbumItemListView(
                                    albumName = album.name,
                                    photoCount = album.images.size,
                                    firstPhotoUri = album.images.getOrNull(0)?.let { Uri.parse(it) }
                                )
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
                            navController.navigate("select_album_to_add_photo")
                        }
                    }
                )
                if (index < fabMenuItems.size - 1) {
                    Divider(color = BlueSecondary, thickness = 1.dp)
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
                                color = Color.White
                            )
                        },
                        modifier= Modifier.fillMaxWidth(),

                        )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (albumNameInput.isNotBlank()) {
                            albumModel.addAlbum(email, albumNameInput)
                            albumNameInput = ""
                            showAlertDialog = false
                        } else {
                            // Handle the case where the album name is blank (e.g., show an error message)
                        }


                    }
                ) {
                    Text("Create",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp)
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
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp)
                    )

                }
            },
            containerColor = Color.White

        )
    }else if (showLongPressDialog) {
        val isDefaultAlbum = selectedAlbum?.name == "DefaultVault" // Kiểm tra album có phải default không
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
                                navController.navigate("select_album_to_add_photo") // Điều hướng đến thêm ảnh
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
                                    renameAlbumInput = selectedAlbum?.name.orEmpty() // Gán tên album hiện tại
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
                                    selectedAlbum?.let {
                                        albumModel.deleteAlbum(email, it.id.toString())
                                    }
                                    showLongPressDialog = false
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
                        if (renameAlbumInput.isNotBlank() && selectedAlbum != null) {
                            albumModel.renameAlbum(email, selectedAlbum!!.name, renameAlbumInput)
                            renameAlbumInput = ""
                            showAlertDialogRename = false
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
