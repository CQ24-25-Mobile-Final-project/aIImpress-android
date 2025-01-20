package com.hcmus.ui.Trash

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hcmus.data.model.Trash
import com.hcmus.data.TrashFirestoreService

class TrashViewModel : ViewModel() {
    private val _trashList = MutableLiveData<List<Trash>>(emptyList())
    val trashList: LiveData<List<Trash>> = _trashList

    private val trashService = TrashFirestoreService()

    // 📌 Thêm ảnh vào Trash + Lưu vào Firestore
    fun addToTrash(email: String, images: List<String>) {
        if (images.isEmpty()) return

        val newTrash = Trash(
            id = System.currentTimeMillis().toString(), // 🔹 Đảm bảo ID là String
            images = images
        )

        trashService.addToTrash(email, newTrash) { // ✅ Bây giờ khớp với phương thức sửa đổi
            val currentTrashList = _trashList.value?.toMutableList() ?: mutableListOf()
            currentTrashList.add(newTrash)
            _trashList.value = currentTrashList
        }
    }

    // 📌 Tải danh sách ảnh từ Firestore
    fun loadTrashFromFirebase(email: String) {
        trashService.getTrash(email) { trashItems ->
            val decodedTrashItems = trashItems.map { trash ->
                Trash(images = trash.images.map { Uri.decode(it) }) // Giải mã URL
            }
            _trashList.value = decodedTrashItems
        }
    }

    // 📌 Xóa ảnh khỏi Trash (khi khôi phục hoặc xóa vĩnh viễn)
    fun removeFromTrash(email: String, trashId: String) {
        val currentTrashList = _trashList.value?.toMutableList() ?: return

        val targetTrash = currentTrashList.find { it.id.toString() == trashId }
        if (targetTrash != null) {
            trashService.deleteFromTrash(email, targetTrash.id) { // ✅ Không còn lỗi type mismatch
                currentTrashList.remove(targetTrash)
                _trashList.value = currentTrashList
            }
        }
    }

    // 📌 Xóa ảnh đã hết hạn (sau 30 ngày)
    fun clearExpiredItems(email: String) {
        trashService.clearExpiredTrash(email) {
            val currentTrashList = _trashList.value?.toMutableList()
            if (currentTrashList != null) {
                val validItems = currentTrashList.filterNot { it.isExpired() }
                _trashList.value = validItems
            }
        }
    }
}
