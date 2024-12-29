package com.hcmus.ui.display

import android.content.Context
import android.content.pm.PackageManager
import android.media.ExifInterface
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

import java.io.IOException

fun getLocationFromExif(context: Context, imageUri: Uri): LatLng? {
    try {
        // Open InputStream using ContentResolver
        context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
            val exif = ExifInterface(inputStream)

            // Retrieve GPS data
            val latLong = FloatArray(2)
            if (exif.getLatLong(latLong)) {
                // Convert the latitude and longitude to decimal if they are available
                val latitude = latLong[0].toDouble()
                val longitude = latLong[1].toDouble()

                // Ensure the latitude and longitude are not zero
                if (latitude != 0.0 && longitude != 0.0) {
                    return LatLng(latitude, longitude)
                } else {
                    println("No valid GPS data found for URI: $imageUri")
                }
            } else {
                println("No GPS metadata found for URI: $imageUri")
            }
        } ?: println("Could not open InputStream for URI: $imageUri")
    } catch (e: IOException) {
        println("Error reading EXIF data for URI: $imageUri. Error: ${e.message}")
    }
    return null
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestLocationPermission() {
    val context = LocalContext.current
    val permissions = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val permissionState = rememberMultiplePermissionsState(permissions.toList())

    // Kiểm tra trạng thái quyền
    val hasPermission = permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    // Nếu chưa có quyền, yêu cầu quyền truy cập
    if (!hasPermission) {
        LaunchedEffect(Unit) {
            permissionState.launchMultiplePermissionRequest()
        }
    }
}


@Composable
fun MapPhotoView(photos: List<Uri>) {
    val context = LocalContext.current

    // Convert URIs to PhotoData with GPS coordinates
    val photoDataList = remember(photos) {
        photos.mapNotNull { uri ->
            val location = getLocationFromExif(context, uri)
            if (location != null) {
                println("GPS location for $uri: Latitude = ${location.latitude}, Longitude = ${location.longitude}")
                PhotoData(
                    title = "Photo Title", // Replace with actual title if available
                    date = "Date", // Replace with actual date if available
                    imageUri = uri,
                    location = location
                )
            } else {
                println("No GPS data found for photo: $uri")
                null
            }
        }
    }

    // Lấy vị trí đầu tiên có GPS, nếu không có thì dùng vị trí mặc định
    val initialLocation = photoDataList.firstOrNull()?.location
    val cameraPositionState = rememberCameraPositionState {
        initialLocation?.let {
            position = CameraPosition.fromLatLngZoom(it, 10f)
        }
    }


    // Yêu cầu quyền truy cập vị trí nếu chưa có
    RequestLocationPermission()

    Column(modifier = Modifier.fillMaxSize()) {
        // Hiển thị Google Map
        Box(modifier = Modifier.weight(1f)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                photoDataList.forEach { photo ->
                    Marker(
                        state = MarkerState(position = photo.location),
                        title = photo.title,
                        snippet = photo.date
                    )
                }
            }
        }

        // Hiển thị danh sách ảnh bên dưới
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(photoDataList) { photo ->
                Image(
                    painter = rememberImagePainter(photo.imageUri),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            println("Photo clicked: Title = ${photo.title}, Latitude = ${photo.location.latitude}, Longitude = ${photo.location.longitude}")
                            cameraPositionState.move(
                                CameraUpdateFactory.newLatLngZoom(photo.location, 15f)
                            )
                        },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}





// Data class lưu thông tin ảnh
data class PhotoData(
    val title: String,
    val date: String,
    val imageUri: Uri,
    val location: LatLng
)
