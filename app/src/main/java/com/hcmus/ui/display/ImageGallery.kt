package com.hcmus.ui.display

import android.content.Context
import android.net.Uri
import android.provider.MediaStore.Audio.Media
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.insets.LocalWindowInsets
import com.hcmus.R
import com.hcmus.data.StorageService
import com.hcmus.ui.components.CustomBottomBar
import com.hcmus.ui.components.GalleryTopBar
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoGalleryScreen(navController: NavController) {
    var isFilterActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Assuming MediaReader is a custom class for accessing media files
    val mediaReader = remember { MediaReader(context) }
    val photosByDate = remember { mediaReader.getAllMediaFiles() }
    val photosByTag = remember { mediaReader.loadMediaFilesFromStorage() }
    val categorizedPhotos = categorizePhotos(photosByDate)
    val storyItems = getStoryItemsFromPhotos(categorizedPhotos)

    // Filter photos based on search query
    val filteredPhotos = filterPhotos(categorizedPhotos,photosByTag, searchQuery)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)

    ) {
        GalleryTopBar(navController)


        SearchOrFilterBar(
            isFilterActive = isFilterActive,
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onFilterToggle = { isFilterActive = !isFilterActive }
        )
        val storyItems = getStoryItemsFromPhotos(categorizedPhotos)

        StoryItemView(storyItems, navController)


        // Displaying the photo gallery
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            filteredPhotos.forEach { (date, photos) ->
                item {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                }

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
                                AsyncImage(
                                    model = photo.uri,
                                    modifier = Modifier.fillMaxSize(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        repeat(3 - rowPhotos.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        CustomBottomBar(
            selectedIndex = 0,
            onTabSelected = { index -> },
            onAddClick = { navController.navigate("appContent") },
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
fun StoryItemView(stories: List<StoryItem>, navController: NavController) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp), // Increase space between items
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp)
    ) {
        items(stories) { story ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    navController.navigate("storyUI/${story.label}")
                }
            ) {
                Box(
                    modifier = Modifier
                        .size(65.dp) // This will be the circular box
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.background)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(story.imageUri),
                        contentDescription = story.label,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(55.dp).clip(CircleShape)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))

                // Ensure the text is centered and truncated with ellipsis if too long
                Text(
                    text = story.label,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    maxLines = 1, // Ensure single line of text
                    overflow = TextOverflow.Ellipsis, // Truncate with ellipsis if text is too long
                    modifier = Modifier
                        .width(70.dp) // Ensure text width matches the circle
                        .padding(horizontal = 4.dp) // Padding to prevent touching the edges
                )
            }
        }
    }
}


data class Photo(
    val uri: Uri,
    val date: Date,
    val label: String,
    val tag: String,
)



data class StoryItem(
    val imageUri: Uri,
    val label: String
)

// Function to categorize photos based on date
fun categorizePhotos(photos: Map<String, List<MediaFile>>): Map<String, List<Photo>> {
    val categorizedPhotos = mutableMapOf<String, MutableList<Photo>>()
    val calendar = Calendar.getInstance()
    val currentDate = calendar.time

    // Categorizing photos based on the number of days ago
    photos.forEach { (date, mediaFiles) ->
        mediaFiles.forEach { mediaFile ->
            val photoDate = Date(mediaFile.dateAdded * 1000L) // Convert timestamp to Date
            val diffInMillis = currentDate.time - photoDate.time
            val diffInDays = diffInMillis / (1000 * 60 * 60 * 24)

            // Categorizing by the number of days ago
            val category = when {
                diffInDays < 1 -> "Hôm nay" // Today
                diffInDays < 2 -> "Hôm qua" // Yesterday
                diffInDays < 7 -> "Vài ngày trước" // A few days ago
                diffInDays < 30 -> "Vài tuần trước" // A few weeks ago
                diffInDays < 365 -> "Vài tháng trước" // A few months ago
                else -> "Vài năm trước" // A few years ago
            }
            val tag = ""

            // Add the photo to the categorized list
            categorizedPhotos.computeIfAbsent(category) { mutableListOf() }
                .add(Photo(uri = mediaFile.url ?: mediaFile.uri, date = photoDate, label =category, tag = tag))
        }
    }

    return categorizedPhotos
}

// Function to create story items from categorized photos
fun getStoryItemsFromPhotos(categorizedPhotos: Map<String, List<Photo>>): List<StoryItem> {
    return categorizedPhotos.mapNotNull { (category, photos) ->
        val photo = photos.firstOrNull() // Choose the first photo in the category (or apply your logic)
        photo?.let {
            StoryItem(imageUri = it.uri, label = category) // Create a StoryItem with the first photo of the category
        }
    }
}

fun filterPhotosByDate(categorizedPhotos: Map<String, List<Photo>>, searchQuery: String): Map<String, List<Photo>> {
    return categorizedPhotos.mapValues { (date, photos) ->
        photos.filter { photo ->
            val queryLower = searchQuery.lowercase()

            // Convert the Date to a string in a specific format for comparison
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val photoDateString = dateFormat.format(photo.date)

            // Extract day, month, and year from photo date
            val calendar = Calendar.getInstance().apply { time = photo.date }
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH) + 1 // Months are 0-based
            val year = calendar.get(Calendar.YEAR)

            // Pattern matching for day, month, year queries
            val matchesDay = "ngày $day" in queryLower
            val matchesMonth = "tháng $month" in queryLower
            val matchesDayMonth = "ngày $day tháng $month" in queryLower
            val matchesYear = "năm $year" in queryLower

            // Check if the search query matches the label or the formatted date
            val matchesLabel = photo.label.lowercase().contains(queryLower)
            val matchesDate = photoDateString.contains(queryLower)

            // Return true if any condition matches
            matchesLabel || matchesDate || matchesDay || matchesMonth || matchesDayMonth || matchesYear
        }
    }.filter { (_, photos) -> photos.isNotEmpty() }
}

fun filterPhotosByTag(categorizedPhotos: List<MediaFile>, searchQuery: String): Map<String, List<Photo>> {
    // Chuyển đổi từ MediaFile sang Photo
    val photos = categorizedPhotos.map { mediaFile ->
        Photo(
            uri = mediaFile.uri,
            date = Date(mediaFile.dateAdded * 1000L), // Chuyển từ timestamp sang Date
            label = mediaFile.name,
            tag = mediaFile.tag
        )
    }

    // Lọc ảnh theo tag
    val queryLower = searchQuery.lowercase()
    val filteredPhotos = photos.filter { photo ->
        photo.tag.lowercase().contains(queryLower)
    }

    // Nhóm các ảnh đã lọc theo ngày
    return filteredPhotos.groupBy { photo ->
        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(photo.date)
    }
}


fun filterPhotos(categorizedPhotos: Map<String, List<Photo>>, categorizedPhotosTag: List<MediaFile>, searchQuery: String): Map<String, List<Photo>> {
    val filteredByDate = filterPhotosByDate(categorizedPhotos, searchQuery)
    val filteredByTag = filterPhotosByTag(categorizedPhotosTag, searchQuery)

    // Combine the results from both filters
    val combinedResults = mutableMapOf<String, List<Photo>>()

    // Add photos filtered by date
    filteredByDate.forEach { (date, photos) ->
        combinedResults[date] = combinedResults.getOrDefault(date, emptyList()) + photos
    }

    // Add photos filtered by tag
    filteredByTag.forEach { (date, photos) ->
        combinedResults[date] = combinedResults.getOrDefault(date, emptyList()) + photos
    }

    // Remove duplicates
    combinedResults.forEach { (date, photos) ->
        combinedResults[date] = photos.distinctBy { it.uri }
    }

    return combinedResults.filter { (_, photos) -> photos.isNotEmpty() }
}