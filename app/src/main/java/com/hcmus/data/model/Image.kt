package com.hcmus.data.model

import com.google.android.gms.maps.model.LatLng
import java.net.URI
import java.time.Instant
import java.util.UUID

data class Image(
  val userId: UUID,
  val name: String,
  val dateAdded: Instant,
  val uri: URI,
  val location: LatLng
)