package com.hcmus

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter


import com.google.accompanist.insets.LocalWindowInsets
import com.hcmus.ui.theme.PhotoappTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoGalleryScreen(navController: NavController) {
    var isFilterActive by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val context = LocalContext.current
    val mediaReader = remember { MediaReader(context) }
    val photosByDate = remember { mediaReader.getAllMediaFiles() } // Phân nhóm ảnh theo ngày

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = with(LocalDensity.current) { LocalWindowInsets.current.statusBars.top.toDp() },
                bottom = with(LocalDensity.current) { LocalWindowInsets.current.navigationBars.bottom.toDp() }
            )
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        GalleryTopBar()

        // Search or Filter Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isFilterActive) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                    val categories = listOf("Favorites", "Selfies", "Travel", "Family")
                    items(categories) { category ->
                        Text(
                            text = category,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search your photos", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon", tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary)
                    .clickable { isFilterActive = !isFilterActive },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.filter_icon),
                    contentDescription = "Filter Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            photosByDate.forEach { (date, photos) ->
                // Hiển thị tiêu đề ngày
                item {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                // Nhóm ảnh theo hàng (3 ảnh/hàng)
                items(photos.chunked(3)) { rowPhotos ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),

                    ) {
                        rowPhotos.forEach { photo ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)

                                    .background(Color.White)
                                    .clickable {
                                        navController.navigate("imageDetail/${Uri.encode(photo.uri.toString())}")
                                    }
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(photo.uri),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        // Thêm ô trống nếu số ảnh không đủ 3 trong hàng
                        repeat(3 - rowPhotos.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }


        // Bottom Navigation Bar
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
            navController = navController,
        )
    }
}

data class StoryItem(
    val imageRes: Int,
    val label: String
)
@Composable
fun StoryItemView(story: StoryItem, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(65.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.background)
                .border(2.dp, MaterialTheme.colorScheme.secondary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = story.imageRes),
                contentDescription = story.label,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(55.dp).clip(CircleShape)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = story.label,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryTopBar() {
    TopAppBar(
        modifier = Modifier
            .statusBarsPadding(), // Đẩy TopAppBar sát vào vùng status bar
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo sát trái
                Box(
                    modifier = Modifier
                        .height(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "App Logo",
                        modifier = Modifier.height(20.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        },
        actions = {
            // Icon dấu cộng
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Avatar
            IconButton(onClick = { /* Profile action */ }) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray) // Thêm nền làm ảnh đại diện nếu không load được
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.avatar),
                        contentDescription = "User Profile",
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}



@Composable
fun CustomBottomBar(
    onAddClick: () -> Unit, // Event for the add button
    onTabSelected: (Int) -> Unit, // Callback to change the tab
    selectedIndex: Int, // Currently selected tab
    navController: NavController // Thêm navController để điều hướng
) {
    // Get the system insets for the navigation bar
    val insets = LocalWindowInsets.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp) // Height of the Bottom Bar
            .background(Color.White),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Bottom bar row with the icons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp), // Padding for the icons
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomBarItem(
                isSelected = selectedIndex == 0,
                iconRes = R.drawable.gallery_icon,
                contentDescription = "Gallery",
                onClick = {
                    onTabSelected(0)
                    navController.navigate("gallery") // Điều hướng về PhotoGalleryScreen
                }
            )
            BottomBarItem(
                isSelected = selectedIndex == 1,
                iconRes = R.drawable.album_icon,
                contentDescription = "Album",
                onClick = { onTabSelected(1) }
            )

            Spacer(modifier = Modifier.width(64.dp)) // Space for the add button

            BottomBarItem(
                isSelected = selectedIndex == 2,
                iconRes = R.drawable.privacy_icon,
                contentDescription = "Share",
                onClick = { onTabSelected(2) }
            )
            BottomBarItem(
                isSelected = selectedIndex == 3,
                iconRes = R.drawable.people_icon,
                contentDescription = "Profile",
                onClick = { onTabSelected(3) }
            )
        }

        // FloatingActionButton (FAB) positioned above the bottom bar
        FloatingActionButton(
            onClick = onAddClick,
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary, // Color of the FAB
            modifier = Modifier
                .size(52.dp) // Size of the FAB
                .align(Alignment.TopCenter) // Align it in the center
                .offset(y = (-10).dp) // Make it float above the bottom bar
        ) {
            Icon(
                painter = painterResource(id = R.drawable.camera_icon),
                contentDescription = "Camera Icon",
                tint = Color.White,
                modifier = Modifier.size(27.dp)
            )
        }
    }
}



@Composable
fun BottomBarItem(
    isSelected: Boolean,
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(50.dp) // Kích thước của từng mục
            .clip(CircleShape) // Hình dạng tròn
            .background(if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent) // Màu nền nếu được chọn
            .clickable(onClick = onClick), // Xử lý click
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary, // Màu sắc khi được chọn
            modifier = Modifier.size(27.dp) // Kích thước biểu tượng
        )
    }
}





@Composable
fun PhotoCard(photoRes: Int, onClick: () -> Unit) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .padding(8.dp)
            .size(150.dp)
            .clickable(onClick = onClick) // Handle click event
    ) {
        Image(
            painter = painterResource(id = photoRes),
            contentDescription = "Photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GalleryPreview() {
    PhotoappTheme {
        val navController = rememberNavController()

        // Không cần sử dụng padding(it) nếu không dùng Scaffold
        PhotoGalleryScreen(navController = navController)
    }
}