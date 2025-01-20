package com.hcmus.ui.edituser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionScreen() {
    var selectedLanguage by remember { mutableStateOf("English (UK)") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Language", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back navigation */ }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .background(Color.White)
        ) {
            Text(
                text = "Suggested",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            LanguageGroup(
                languages = listOf("English (US)", "English (UK)"),
                selectedLanguage = selectedLanguage,
                onLanguageSelected = { selectedLanguage = it }
            )
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            Text(
                text = "Others",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            LanguageGroup(
                languages = listOf(
                    "Mandarin", "Hindi", "Spanish", "French",
                    "Arabic", "Russian", "Indonesian", "Vietnamese"
                ),
                selectedLanguage = selectedLanguage,
                onLanguageSelected = { selectedLanguage = it }
            )
        }
    }
}

@Composable
fun LanguageGroup(
    languages: List<String>,
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    Column(modifier = Modifier.selectableGroup()) {
        languages.forEach { language ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (language == selectedLanguage),
                        onClick = { onLanguageSelected(language) }
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = language,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                RadioButton(
                    selected = (language == selectedLanguage),
                    onClick = { onLanguageSelected(language) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLanguageSelectionScreen() {
    LanguageSelectionScreen()
}
