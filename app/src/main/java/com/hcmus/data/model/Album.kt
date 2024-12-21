package com.hcmus.data.model

import java.util.UUID

data class Album(
  val id: UUID = UUID.randomUUID(),
  val userId: UUID
)