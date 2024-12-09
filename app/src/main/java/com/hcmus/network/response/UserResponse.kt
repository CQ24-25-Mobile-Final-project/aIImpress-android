package com.hcmus.network.response

import com.hcmus.data.model.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
  @SerialName("user") val user: User,
  @SerialName("token") val token: TokenResponse
)