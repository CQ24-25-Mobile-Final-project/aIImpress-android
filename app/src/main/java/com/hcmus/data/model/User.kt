package com.hcmus.data.model

import java.util.UUID

data class User(
  val id: UUID = UUID.randomUUID(),
  val email: String,
)