package com.hcmus.data.model

import java.time.LocalDateTime
import java.util.UUID

data class Trash(
    val id: UUID = UUID.randomUUID(),               // ID duy nhất cho Trash entry
    val images: List<String> = listOf(),                      // Danh sách URI của các ảnh bị xóa
    val deletedAt: LocalDateTime = LocalDateTime.now(), // Thời gian xóa
    val expiresAt: LocalDateTime = deletedAt.plusDays(30) // Thời gian hết hạn (sau 30 ngày)
) {
    // Kiểm tra xem Trash đã hết hạn chưa
    fun isExpired(currentTime: LocalDateTime = LocalDateTime.now()): Boolean {
        return currentTime.isAfter(expiresAt)
    }
}
