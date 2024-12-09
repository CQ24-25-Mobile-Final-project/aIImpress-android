package com.hcmus.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserRegisterRequest(
  @SerialName("username") val username: String,
  @SerialName("email") val email: String?,
  @SerialName("password") val password: String,
  @SerialName("phone") val phone: String,
  @SerialName("otp") val otp: String,
)