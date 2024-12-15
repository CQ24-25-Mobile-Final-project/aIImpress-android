import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hcmus.ui.display.PhotoDetails
import com.hcmus.ui.display.getPhotoDetails
import com.hcmus.ui.display.setAsWallpaper

@Composable
fun showMoreOptions(navController: NavController, photoUri: String, context: Context) {
    val options = listOf("Add to Favorites", "Set as Wallpaper", "Details")

    val showDialog = remember { mutableStateOf(true) }
    val showDetailsDialog = remember { mutableStateOf(false) }
    val photoDetails = remember { mutableStateOf<PhotoDetails?>(null) }

    // Main options dialog
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("More Options") },
            text = {
                Column {
                    options.forEach { option ->
                        Text(
                            text = option,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showDialog.value = false
                                    when (option) {
                                        "Set as Wallpaper" -> setAsWallpaper(context, photoUri)
                                        "Details" -> {
                                            // Extract and show photo details
                                            photoDetails.value = getPhotoDetails(context, photoUri)
                                            showDetailsDialog.value = true
                                        }
                                    }
                                }
                                .padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {}
        )
    }

    // Details dialog
    if (showDetailsDialog.value) {
        AlertDialog(
            onDismissRequest = { showDetailsDialog.value = false },
            title = { Text("Photo Details") },
            text = {
                Column(modifier = Modifier.padding(8.dp)) {
                    val details = photoDetails.value
                    if (details != null) {
                        Text("Date & Time: ${details.dateTime ?: "Unknown"}")
                        Text("ISO: ${details.iso ?: "Unknown"}")
                        Text("Aperture: ${details.aperture ?: "Unknown"}")
                        Text("Exposure Time: ${details.exposureTime ?: "Unknown"}")
                        Text("Focal Length: ${details.focalLength ?: "Unknown"}")
                        Text("Device Model: ${details.model ?: "Unknown"}")
                        Text("Device Make: ${details.make ?: "Unknown"}")
                        Text("File Size: ${details.fileSize ?: "Unknown"}")
                    } else {
                        Text("No details available.")
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showDetailsDialog.value = false }) {
                    Text("Close", color = androidx.compose.ui.graphics.Color.White)
                }
            }
        )
    }
}
