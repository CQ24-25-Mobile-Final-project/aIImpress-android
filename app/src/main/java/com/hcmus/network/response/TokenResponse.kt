package com.hcmus.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
  @SerialName("username") val username: String,
  @SerialName("roles") val roles: List<String>,
  @SerialName("access_token") val accessToken: String,
  @SerialName("refresh_token") val refreshToken: String,
  @SerialName("token_type") val tokenType: String,
  @SerialName("expires_in") val expiresIn: Int
)