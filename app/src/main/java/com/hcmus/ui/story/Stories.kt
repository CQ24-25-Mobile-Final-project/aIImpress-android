import android.net.Uri
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
import androidx.compose.material3.MaterialTheme
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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.hcmus.R
import com.hcmus.ui.display.Photo

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StoryUI(navController: NavHostController, startIndex: Int, photos: List<Photo>) {
    val pagerState = rememberPagerState(initialPage = startIndex, pageCount = { photos.size })
    val coroutineScope = rememberCoroutineScope()
    var currentPage by remember { mutableStateOf(startIndex) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Image Carousel (properly positioned under the header)
        StoryImagePager(
            pagerState = pagerState,
            listOfUri = photos.map { it.uri },
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp) // Ensure images are below the header
        )

        // Progress Indicators
        ProgressIndicators(
            listOfUri = photos.map { it.uri },
            pagerState = pagerState,
            onComplete = {
                coroutineScope.launch {
                    if (pagerState.currentPage < photos.size - 1) {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            }
        )
        StoryHeader(
            label = photos[currentPage].label,
            modifier = Modifier.align(Alignment.TopStart).padding(top = 65.dp, start = 10.dp)
        )
    }

    // Listening to pager state changes
    LaunchedEffect(pagerState.currentPage) {
        currentPage = pagerState.currentPage
    }
}

@Composable
fun StoryHeader(
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .zIndex(1f)
    ) {
        // Display the dynamic story label
        Text(
            text = label,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )
    }
}


@Composable
fun ProgressIndicators(
    listOfUri: List<Uri>,
    pagerState: androidx.compose.foundation.pager.PagerState,
    onComplete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp, top = 55.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (index in listOfUri.indices) {
            LinearIndicator(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                isActive = index == pagerState.currentPage, // Thanh chỉ chạy cho trang hiện tại
                onAnimationEnd = onComplete
            )
        }
    }
}

@Composable
fun LinearIndicator(
    modifier: Modifier,
    isActive: Boolean = false, // Thanh chỉ chạy khi `isActive` là true
    onAnimationEnd: () -> Unit
) {
    var progress by remember { mutableStateOf(0f) }

    // Animation sẽ chạy từ 0 -> 1 khi `isActive = true`
    val animateProgress by animateFloatAsState(
        targetValue = if (isActive) progress else progress,
        animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
    )

    // Kiểm soát hiệu ứng animation
    LaunchedEffect(isActive) {
        if (isActive) {
            progress = 0f // Đặt lại tiến trình về 0
            progress = 1f // Animation chạy từ 0 đến 1
            delay(3000) // Thời gian chạy hiệu ứng

            // Gọi callback khi animation kết thúc
            onAnimationEnd()
        }
    }

    // Hiển thị thanh tiến trình
    Box(
        modifier = modifier
            .height(4.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        // Thanh nền trắng (hiển thị mặc định)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White) // Nền mặc định màu trắng
        )

        // Thanh tiến trình màu xanh chạy đè lên
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animateProgress) // Chạy theo tiến trình
                .background(MaterialTheme.colorScheme.primary) // Màu xanh chạy đè lên nền trắng
        )
    }
}

@Composable
fun StoryImagePager(
    pagerState: androidx.compose.foundation.pager.PagerState,
    listOfUri: List<Uri>,
    modifier: Modifier = Modifier // Add a modifier parameter here
) {
    Box(modifier = modifier) { // Use Box to handle the passed modifier
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize() // Fill the remaining available space
        ) { page ->
            Image(
                painter = rememberAsyncImagePainter(listOfUri[page]),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop // Ensure proper scaling
            )
        }
    }
}

@Composable
fun StoryImage(pagerState: androidx.compose.foundation.pager.PagerState, listOfImage: List<Int>) {
    HorizontalPager(state = pagerState) { page ->
        Image(
            painter = painterResource(id = listOfImage[page]),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
