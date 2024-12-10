/*
package com.example.com.hcmus

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.finalproject.R
import com.google.accompanist.insets.LocalWindowInsets
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StoryUI(navController: NavHostController, startIndex: Int) {
    val photoUris = remember { getPhotoUris() }

    if (photoUris.isNotEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Story Header
            Column {
                Stories(listOfUri = photoUris, startIndex = startIndex)
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No photos available.",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun Stories(listOfUri: List<String>, startIndex: Int) {
    val pagerState = rememberPagerState(initialPage = startIndex, pageCount = { listOfUri.size })
    val coroutineScope = rememberCoroutineScope()
    var currentPage by remember { mutableStateOf(startIndex) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Hiển thị hình ảnh
        StoryImage(pagerState = pagerState, listOfUri = listOfUri)

        // Thanh tiến trình phía trên
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(4.dp, top = 55.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (index in listOfUri.indices) {
                LinearIndicator(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp),
                    isActive = index == currentPage,
                    onAnimationEnd = {
                        coroutineScope.launch {
                            if (currentPage < listOfUri.size - 1) {
                                currentPage++
                                pagerState.animateScrollToPage(currentPage)
                            }
                        }
                    }
                )
            }
        }

        // Header của story
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(4.dp, top = 65.dp)
        ) {
            Spacer(modifier = Modifier.height(5.dp))

            StoryHeader(
                avatarRes = R.drawable.avatar,
                username = "Wnow",
                timePosted = "58w",
                onMuteClick = {
 Xử lý Mute
 },
                onMoreOptionsClick = {
 Xử lý More Options
 }
            )
        }
    }

    // Lắng nghe sự kiện chuyển trang
    LaunchedEffect(pagerState.currentPage) {
        currentPage = pagerState.currentPage
    }
}

@Composable
fun StoryImage(pagerState: androidx.compose.foundation.pager.PagerState, listOfUri: List<String>) {
    HorizontalPager(state = pagerState) { page ->
        Image(
            painter = rememberImagePainter(data = listOfUri[page]),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}


*/
