package com.hcmus.data

object FavoritesManager {
    private val favoriteImages = mutableListOf<String>()

    fun addToFavorites(photoUri: String) {
        if (!favoriteImages.contains(photoUri)) {
            favoriteImages.add(photoUri)
        }
    }

    fun removeFromFavorites(photoUri: String) {
        favoriteImages.remove(photoUri)
    }

    fun isFavorite(photoUri: String): Boolean {
        return favoriteImages.contains(photoUri)
    }

    fun getFavorites(): List<String> = favoriteImages
}
