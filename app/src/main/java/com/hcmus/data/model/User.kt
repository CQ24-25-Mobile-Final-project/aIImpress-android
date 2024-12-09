package com.hcmus.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
  @SerialName("id") val id: String,
  @SerialName("username") val username: String,
  @SerialName("phone") val phone: String? = null,
  @SerialName("email") val email: String?,
  @SerialName("first_name") val firstName: String? = null,
  @SerialName("last_name") val lastName: String? = null,
  @SerialName("avatar_url") val avatarUrl: String? = null,
  @SerialName("bio") val bio: String? = null,
)