package com.hcmus.ui.album

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.galleryapp.ui.components.MyTopAppBar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.hcmus.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectImageForAlbum(navController: NavController) {
    val photos = listOf(
        R.drawable.wallpaper, R.drawable.photo2, R.drawable.photo3
    )
    val selectedPhotos = remember { mutableStateListOf<Int>() }
    val queryState = remember { mutableStateOf("") }

    Scaffold (
        topBar = {
            MyTopAppBar(
                title = "Add photos",
                titleLeftButton = "Cancel",
                onNavigationClick = { navController.popBackStack() },
                onActionClick = { /* Handle action click */ },
                actionIcon = Icons.Default.Done,
                menuItems = listOf()
            )
        }
    ) {
        paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchBar(
                query = queryState.value,
                onQueryChange = { newQuery -> queryState.value = newQuery },
                onSearch = { /* Handle search */ },
                active = false,
                onActiveChange = { /* Handle active change */ },
                modifier = Modifier.padding(16.dp)
                    .fillMaxWidth(),
                placeholder = {
                    Text(text = "Search your photos", color = Color.Gray) // Text dưới dạng hint
                },
                leadingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                },
            ) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                Text(text ="Search")
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(3) { index ->
                    val photoId = photos[index]
                    val isSelected = selectedPhotos.contains(photoId)

                    BoxWithConstraints(
                        modifier = Modifier.fillMaxWidth()
                            .fillMaxHeight() // Sử dụng toàn bộ không gian có sẵn
                            .toggleable(
                                value = isSelected,
                                onValueChange = {
                                    if (isSelected) {
                                        selectedPhotos.remove(photoId)
                                    } else {
                                        selectedPhotos.add(photoId)
                                    }
                                }
                            )
                    ) {
                        val screenWidth = maxWidth

                        Image(
                            painter = painterResource(id = R.drawable.wallpaper),
                            contentDescription = null,
                            modifier = Modifier
                                .size(screenWidth * 1f)
                        )

                        if (isSelected) {
                            Box (
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(Color.White.copy(alpha = 0.3f))
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .border(
                                            width = 2.dp, // Độ dày của viền
                                            color = Color.White, // Màu sắc của viền
                                            shape = CircleShape // Đảm bảo viền là hình tròn
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = Color.Blue,
                                        modifier = Modifier.fillMaxSize()
                                    )

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SelectImageForAlbumPreview() {
    val mockNavController = rememberNavController() // Mock NavController cho Preview
    SelectImageForAlbum(navController = mockNavController)
}
