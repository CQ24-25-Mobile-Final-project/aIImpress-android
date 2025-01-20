package com.hcmus.data.model

import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId

data class Trash(
    val id: String = System.currentTimeMillis().toString(), // 🔹 Sử dụng String thay vì UUID
    val images: List<String> = emptyList(),
    val deletedAt: Timestamp = Timestamp.now(),
    val expiresAt: Timestamp = Timestamp(Timestamp.now().seconds + 30 * 24 * 60 * 60, 0) // 30 ngày sau
) {
    constructor() : this("", emptyList(), Timestamp.now(), Timestamp(Timestamp.now().seconds + 30 * 24 * 60 * 60, 0))

    fun getDeletedAtAsLocalDateTime(): LocalDateTime {
        return deletedAt.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    }

    fun getExpiresAtAsLocalDateTime(): LocalDateTime {
        return expiresAt.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    }

    fun isExpired(): Boolean {
        val now = LocalDateTime.now()
        return getExpiresAtAsLocalDateTime().isBefore(now)
    }
}
