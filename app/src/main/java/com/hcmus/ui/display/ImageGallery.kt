package com.hcmus.ui.display

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
import androidx.compose.foundation.lazy.grid.items
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
import com.hcmus.R
import androidx.compose.foundation.layout.* // Make sure this import exists
import com.hcmus.ui.components.CustomBottomBar
import com.hcmus.ui.components.GalleryTopBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoGalleryScreen(navController: NavController) {
    var isFilterActive by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val context = LocalContext.current
    val mediaReader = remember { MediaReader(context) }
    val photosByDate = remember { mediaReader.getAllMediaFiles() }
    val insets = LocalWindowInsets.current
    val bottomInset = with(LocalDensity.current) { insets.navigationBars.bottom.toDp() }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                top = with(LocalDensity.current) { LocalWindowInsets.current.statusBars.top.toDp() },
                bottom = with(LocalDensity.current) { LocalWindowInsets.current.navigationBars.bottom.toDp() }
            )
    ) {
        // Top Bar
        GalleryTopBar()

        // Search or Filter Bar
        SearchOrFilterBar(
            isFilterActive = isFilterActive,
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onFilterToggle = { isFilterActive = !isFilterActive }
        )

        // Photo List
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
            navController = navController
        )
    }
}

@Composable
fun SearchOrFilterBar(
    isFilterActive: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onFilterToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp, start = 8.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isFilterActive) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
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
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search your photos", color = Color.Gray) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search Icon", tint = MaterialTheme.colorScheme.primary)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
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
                .clickable { onFilterToggle() },
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
}

@Composable
fun PhotoThumbnail(photoUri: Uri, navController: NavController) {
    Box(
        modifier = Modifier

            .aspectRatio(1f)
            .background(Color.White)
            .clickable {
                navController.navigate("imageDetail/${Uri.encode(photoUri.toString())}")
            }
    ) {
        Image(
            painter = rememberAsyncImagePainter(photoUri),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

// The remaining composables (CustomBottomBar, BottomBarItem, GalleryTopBar, etc.) remain unchanged.

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
fun PreviewPhotoGalleryScreen() {
    val navController = rememberNavController()

    // Mock data for the preview
    val mockPhotos = listOf(
        Photo("2024-12-14", listOf(
            PhotoItem(Uri.parse("https://www.example.com/photo1.jpg")),
            PhotoItem(Uri.parse("https://www.example.com/photo2.jpg")),
            PhotoItem(Uri.parse("https://www.example.com/photo3.jpg"))
        )),
        Photo("2024-12-13", listOf(
            PhotoItem(Uri.parse("https://www.example.com/photo4.jpg")),
            PhotoItem(Uri.parse("https://www.example.com/photo5.jpg")),
            PhotoItem(Uri.parse("https://www.example.com/photo6.jpg"))
        ))
    )

    // Mocking the data source
    val mediaReader = remember { MockMediaReader(mockPhotos) }

    // Using the gallery screen with mock data
    PhotoGalleryScreen(navController = navController)
}

// Mock data classes to simulate photos
data class PhotoItem(val uri: Uri)
data class Photo(val date: String, val photos: List<PhotoItem>)

// Mock MediaReader to return the mock photos
class MockMediaReader(private val photos: List<Photo>) {
    fun getAllMediaFiles(): List<Photo> = photos
}