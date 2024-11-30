package com.hcmus.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthToken(
  @SerialName("access_token")
  val accessToken: String,
  @SerialName("refresh_token")
  val refreshToken: String
)