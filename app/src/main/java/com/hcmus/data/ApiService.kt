package com.hcmus.data


import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class ImageRequest(val prompt: String)
data class ImageResponse(val image: String)

interface ApiService {
    @POST("generate-image")
    suspend fun generateImage(@Body request: ImageRequest): Response<ImageResponse>
}
