package com.hcmus.domain

sealed class Screen(val route: String){
    object HomeScreen: Screen("Home_Screen")
    object LoadingScreen: Screen("Loading_Screen")
    object ImageScreen: Screen("Image_Screen")
}