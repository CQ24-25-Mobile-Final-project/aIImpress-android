package com.hcmus.data.model

import android.net.Uri
import java.util.UUID

data class Album(
  val id: String = UUID.randomUUID().toString(), // UUID duy nhất cho album
  val name: String = "",
  val images: List<String> = emptyList(),
  val dateCreated: Long = System.currentTimeMillis() // Ngày tạo (timestamp)
)
