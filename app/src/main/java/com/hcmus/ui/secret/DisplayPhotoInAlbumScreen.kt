package com.hcmus.ui.secret

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.net.Uri
import android.util.Log
import androidx.compose.material.icons.filled.CheckCircle
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import com.hcmus.data.ContextStore
import com.hcmus.data.model.Album
import com.hcmus.ui.components.CustomBottomBar

@Composable
fun DisplayPhotoInAlbumScreen(navController: NavController, context: Context, albumName: String) {
    // Giả lập dữ liệu ảnh theo từng album
    var photoList by remember { mutableStateOf<List<Uri>>(emptyList()) }
    // Lấy email từ ContextStore
    val email = remember { ContextStore.get(context, "email") ?: "" }

    // Nếu email không hợp lệ, không tiếp tục
    if (email.isBlank()) {
        Log.e("DisplayPhotoInAlbumScreen", "Email not found in ContextStore")
        return
    }

    // Lấy dữ liệu từ Firestore
    LaunchedEffect(albumName) {
        getPhotosForAlbumFromFirestore(albumName, email) { photos ->
            photoList = photos
        }
    }

    Scaffold(
        bottomBar = {
            var selectedIndex = 2
            CustomBottomBar(
                selectedIndex = selectedIndex,
                onTabSelected = { index ->
                    selectedIndex = index
                    if (index == 3) navController.navigate("shareScreen")
                    if (index == 0) navController.navigate("gallery")
                },
                onAddClick = {
                    navController.navigate("appContent")
                },
                navController = navController
            )
        },
        content = { paddingValues ->
            if (photoList.isEmpty()) {
                // Hiển thị thông báo nếu không có ảnh nào
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Photos or Videos",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Lưới ảnh 3 cột
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(photoList) { photoUri ->
                            Box(
                                modifier = Modifier
                                    .padding(1.dp)
                                    .aspectRatio(1f)
                            ) {
                                AsyncImage( // Dùng Coil để tải ảnh từ Uri
                                    model = photoUri,
                                    contentDescription = "Photo in $albumName",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                    // Thông tin số lượng ảnh và video
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {


                    }
                }
            }
        }
    )
}

fun getPhotosForAlbumFromFirestore(
    albumName: String,
    userEmail: String,
    onResult: (List<Uri>) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
        .collection("android-collection")
        .document(userEmail)
        .collection("albums")

    db.whereEqualTo("name", albumName)
        .get()
        .addOnSuccessListener { querySnapshot ->
            val album = querySnapshot.documents.firstOrNull()?.toObject(Album::class.java)
            val photoUris = album?.images?.map { Uri.parse(it.toString()) } ?: emptyList()
            onResult(photoUris)
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error fetching photos: ${e.message}")
            onResult(emptyList())
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReusableTopBar(
    albumName: String,
    onBackPressed: () -> Unit
) {
    var expanded by remember { mutableStateOf(false)}
    val menuItems = listOf("Select", "Add Items")
    val menuIcons = listOf(
        Icons.Default.CheckCircle,  // Create Folder
        Icons.Filled.Add,       // Add Items
    )
    var showAlertDialog by remember { mutableStateOf(false) }
    TopAppBar(
        title = {
            // Xử lý hiển thị tên album: nếu dài thì dùng ellipsis
            Text(
                text = albumName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp), // Tạo khoảng trống tránh tràn sang các icon
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 1, // Giới hạn 1 dòng
                softWrap = false, // Ngăn text xuống dòng
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis // Cắt với dấu "..."
            )
        },
        navigationIcon = {
            IconButton(onClick = { onBackPressed() }) {
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
                            }
                        }
                    )
                    // Nếu không phải mục cuối, thêm một đường phân cách (divider)
                    if (index < menuItems.size - 1) {
                        Divider(color = Color.LightGray , thickness = 1.dp)
                    }
                }
            }
        },

    )
}
