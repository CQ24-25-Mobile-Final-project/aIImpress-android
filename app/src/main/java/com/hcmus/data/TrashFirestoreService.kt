package com.hcmus.data

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.hcmus.data.model.Trash

class TrashFirestoreService {
    private val db = FirebaseFirestore.getInstance()

    // 📌 Thêm ảnh vào Trash
    fun addToTrash(email: String, trash: Trash, callback: () -> Unit) {
        val trashCollection = db.collection("android-collection").document(email).collection("trash")

        trashCollection.document(trash.id).set(trash)
            .addOnSuccessListener {
                Log.d("Firestore", "Ảnh đã được thêm vào Trash: ${trash.id}")
                callback()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Lỗi thêm ảnh vào Trash: ${e.message}")
            }
    }


    // 📌 Lấy danh sách ảnh từ Trash
    fun getTrash(email: String, callback: (List<Trash>) -> Unit) {
        db.collection("android-collection")
            .document(email)
            .collection("trash")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val trashItems = querySnapshot.documents.mapNotNull { document ->
                    document.toObject<Trash>()
                }
                callback(trashItems)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Lỗi lấy dữ liệu Trash: ${e.message}")
                callback(emptyList())
            }
    }

    // 📌 Xóa ảnh khỏi Trash theo ID (String)
    fun deleteFromTrash(email: String, trashId: String, onComplete: () -> Unit) { // 🔹 Chuyển đổi UUID -> String
        db.collection("android-collection")
            .document(email)
            .collection("trash")
            .document(trashId) // 🔹 Sử dụng String trực tiếp
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Ảnh đã được xóa khỏi Trash!")
                onComplete()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Lỗi khi xóa ảnh khỏi Trash: ${e.message}")
            }
    }

    // 📌 Xóa ảnh hết hạn trong Trash
    fun clearExpiredTrash(email: String, onComplete: () -> Unit) {
        val currentTime = Timestamp.now()

        db.collection("android-collection")
            .document(email)
            .collection("trash")
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.documents.forEach { document ->
                    val trashItem = document.toObject<Trash>()
                    if (trashItem != null && trashItem.expiresAt.seconds < currentTime.seconds) {
                        db.collection("android-collection")
                            .document(email)
                            .collection("trash")
                            .document(document.id)
                            .delete()
                            .addOnSuccessListener {
                                Log.d("Firestore", "Đã xóa ảnh hết hạn trong Trash: ${document.id}")
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Lỗi khi xóa ảnh hết hạn: ${e.message}")
                            }
                    }
                }
                onComplete()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Lỗi khi kiểm tra Trash: ${e.message}")
            }
    }
}
