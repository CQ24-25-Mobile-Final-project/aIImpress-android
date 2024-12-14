package com.hcmus.ui.aiimpress

import com.hcmus.ui.theme.MyApplicationTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AIImpressScreen(modifier: Modifier = Modifier, navController: NavHostController, viewModel: AIImpressViewModel = hiltViewModel()) {
    val items by viewModel.uiState.collectAsStateWithLifecycle()
    if (items is AIImpressUiState.Success) {
        AIImpressScreen(
            items = (items as AIImpressUiState.Success).data,
            onSave = viewModel::addAIImpress,
            navController = navController,
            modifier = modifier
        )
    }
}

@Composable
internal fun AIImpressScreen(
    items: List<String>,
    onSave: (name: String) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        var nameAIImpress by remember { mutableStateOf("Compose") }
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = nameAIImpress,
                onValueChange = { nameAIImpress = it }
            )

            Button(modifier = Modifier.width(96.dp), onClick = { onSave(nameAIImpress) }) {
                Text("Save")
            }
        }

        items.forEach {
            Text("Saved item: $it")
        }

        // Thêm một nút mới để điều hướng sang màn hình nhập mật khẩu
        Button(onClick = { navController.navigate("authentication") }) {
            Text(text = "Go to Password Screen")
        }
    }
}

// Previews

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MyApplicationTheme {
        AIImpressScreen(
            listOf("Compose", "Room", "Kotlin"),
            onSave = {},
            navController = NavHostController(context = LocalContext.current) // Mock NavController for preview
        )
    }
}

@Preview(showBackground = true, widthDp = 480)
@Composable
private fun PortraitPreview() {
    MyApplicationTheme {
        AIImpressScreen(
            listOf("Compose", "Room", "Kotlin"),
            onSave = {},
            navController = NavHostController(context = LocalContext.current) // Mock NavController for preview
        )
    }
}
