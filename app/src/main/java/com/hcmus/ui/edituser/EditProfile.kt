package com.hcmus.ui.edituser

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.hcmus.data.model.Gender
import com.hcmus.data.model.Profile
import com.hcmus.ui.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController, profileViewModel: ProfileViewModel = viewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val profileState = remember { mutableStateOf<Profile?>(null) }
    val showSuccessMessage = remember { mutableStateOf(false) }

    // Get the current user's email
    val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

    // Load profile data when the screen is displayed
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val profile =
                currentUserEmail?.let { profileViewModel.getProfileByEmail(it) } // Assuming profile ID is 1
            profileState.value = profile
        }
    }

    val fullNameState = remember { mutableStateOf(TextFieldValue("")) }
    val emailState = remember { mutableStateOf(TextFieldValue(currentUserEmail ?: "")) }
    val phoneNumberState = remember { mutableStateOf(TextFieldValue("")) }
    val countryState = remember { mutableStateOf("") }
    val genderState = remember { mutableStateOf(Gender.Male) }

    // Update the states when profile data is available
    LaunchedEffect(profileState.value) {
        profileState.value?.let { profile ->
            fullNameState.value = TextFieldValue(profile.fullName)
            emailState.value = TextFieldValue(profile.email)
            phoneNumberState.value = TextFieldValue(profile.phoneNumber)
            countryState.value = profile.country
            genderState.value = profile.gender
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
                .background(Color(0xFFF8F8F8)),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AvatarSection()
            EditableTextField(label = "Full name", textState = fullNameState)
            NonEditableTextField(label = "Email", textState = emailState)
            EditableTextField(label = "Phone number", textState = phoneNumberState)
            DropdownMenuField(label = "Country", options = listOf("United States", "Việt Nam"), selectedOption = countryState)
            DropdownMenuField(label = "Gender", options = Gender.values().toList(), selectedOption = genderState)
            Spacer(modifier = Modifier.weight(1f))
            SubmitButton(
                onClick = {
                    val profile = Profile(
                        id = profileState.value?.id ?: 0, 
                        fullName = fullNameState.value.text,
                        email = emailState.value.text,
                        phoneNumber = phoneNumberState.value.text,
                        country = countryState.value,
                        gender = genderState.value
                    )
                    profileViewModel.insert(profile)
                }
            )
        }
    }
}

@Composable
fun AvatarSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color.Gray, shape = RoundedCornerShape(60.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder for avatar
            Text("Avatar", color = Color.White)
        }
        Button(
            onClick = { /* Handle change avatar */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Change Avatar", color = Color.White)
        }
    }
}

@Composable
fun EditableTextField(label: String, textState: MutableState<TextFieldValue>) {
    OutlinedTextField(
        value = textState.value,
        onValueChange = { textState.value = it },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        singleLine = true
    )
}

@Composable
fun NonEditableTextField(label: String, textState: MutableState<TextFieldValue>) {
    OutlinedTextField(
        value = textState.value,
        onValueChange = {},
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        enabled = false
    )
}

@Composable
fun <T> DropdownMenuField(label: String, options: List<T>, selectedOption: MutableState<T>) {
    val expanded = remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedOption.value.toString(),
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                IconButton(onClick = { expanded.value = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        )
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        selectedOption.value = option
                        expanded.value = false
                    },
                    text = { Text(option.toString()) }
                )
            }
        }
    }
}

@Composable
fun SubmitButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF))
    ) {
        Text("SUBMIT", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}
