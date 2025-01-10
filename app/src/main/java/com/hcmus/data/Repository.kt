package com.hcmus.data

interface ImageRepository {

    suspend fun generateImage(prompt: String): Result<String>
}

