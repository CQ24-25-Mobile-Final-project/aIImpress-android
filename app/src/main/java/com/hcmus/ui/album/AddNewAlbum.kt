package com.hcmus.ui.album

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hcmus.ui.components.GalleryTopBar


@Composable
fun AddNewAlbum(navController: NavController) {
    var albumName by remember{ mutableStateOf("") }
    Scaffold(
        topBar = {
            GalleryTopBar()
        }
    ) {
        paddingValues ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp)
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            TextField(
                value = albumName,
                onValueChange = { albumName = it },
                label = {
                    Text(
                        text ="Add a title",
                        style = typography.titleLarge,
                        color = Color.Gray
                    ) },
                modifier = Modifier.fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(12.dp)),
                singleLine = true,
//                colors = textFieldColors(
//                    focusedIndicatorColor = Color.Transparent, // No underline when focused
//                    unfocusedIndicatorColor = Color.Transparent, // No underline when not focused
//                )
            )
            Spacer(modifier = Modifier.height(250.dp))
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Add photos",
                modifier = Modifier.padding(8.dp))
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .height(90.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Select people & pets",
                        modifier = Modifier.padding(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = "Select people & pets",
                            style = typography.titleLarge
                        )
                        Text(
                            text = "Create an auto-updating album",
                            style = typography.titleSmall
                        )
                    }
                }
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .height(90.dp)
                        .clickable {
                            try {
                                AlbumRepository.addAlbumName(albumName)
                                navController.navigate("SelectImageForAlbum")
                            } catch (e: Exception) {
                                Log.e("Navigation Error", e.message.toString())
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Select photo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(24.dp)
                    )
                    Column {
                        Text(
                            text = "Select photo",
                            style = typography.titleLarge
                        )
                    }
                }
            }
        }
    }
}
