package com.hcmus.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import com.hcmus.domain.Screen
import com.hcmus.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryTopBar(
  navController: NavController, onActionClick: (() -> Unit)? = null,
) {
  TopAppBar(
    modifier = Modifier.fillMaxWidth(),
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier.height(56.dp),
          contentAlignment = Alignment.Center
        ) {
          Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "App Logo",
            modifier = Modifier.height(20.dp)
          )
        }
        Spacer(modifier = Modifier.weight(1f))
      }
    },
    actions = {
      IconButton(onClick = {
        navController.navigate(Screen.HomeScreen.route) // Điều hướng sang HomeScreen
      }) {
      Icon(
          imageVector = Icons.Default.Add,
          contentDescription = "Add Icon",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(24.dp)
        )
      }
      IconButton(onClick = { navController.navigate("editUser") }) {
        Box(
          modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
              .background(Color.LightGray)
        ) {
          Image(
            painter = painterResource(id = R.drawable.avatar),
            contentDescription = "User Profile",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )
        }
      }
    }
  )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
  title: String,
  titleLeftButton: String,
  onNavigationClick: () -> Unit,
  onActionClick: () -> Unit,
  actionIcon: ImageVector,
  menuItems: List<Pair<String, () -> Unit>>,
) {
  val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
  val showMenu = remember { mutableStateOf(false) }
  CenterAlignedTopAppBar(
    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
      titleContentColor = MaterialTheme.colorScheme.primary,
    ),
    title = {
      Text(
        text = title,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 26.sp
      )
    },
    navigationIcon = {
      IconButton(
        onClick = { onNavigationClick() },
        modifier = Modifier.width(100.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clickable { onNavigationClick() },
        ) {
          Icon(
            imageVector = Icons.Default.KeyboardArrowLeft,
            contentDescription = "Back",
            modifier = Modifier.size(32.dp)
          )
          Text(
            text = titleLeftButton,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }
    },
    actions = {
      IconButton(
        onClick = {
          if (actionIcon == Icons.Default.MoreVert) {
            showMenu.value = !showMenu.value
          } else {
            onActionClick()
          }
        },
      ) {
        Icon(
          imageVector = actionIcon,
          contentDescription = null
        )
      }
      if (actionIcon == Icons.Default.MoreVert) {
        DropdownMenu(
          modifier = Modifier.clip(RoundedCornerShape(12.dp)),
          expanded = showMenu.value,
          onDismissRequest = { showMenu.value = false },
          properties = PopupProperties(
            dismissOnClickOutside = true
          )
        ) {
          menuItems.forEach { menuItem ->
            DropdownMenuItem(
              text = { Text(menuItem.first) },
              onClick = {
                menuItem.second() // Gọi hàm onClick của mục menu
                showMenu.value = false
              }
            )
          }
        }
      }
    },
    scrollBehavior = scrollBehavior,
  )
}


