package com.hcmus.data

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.hcmus.data.model.Profile
import kotlinx.coroutines.tasks.await
import java.io.File

class FirebaseProfileRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val profilesCollection = firestore.collection("profiles")
    private val storageRef = storage.reference

    suspend fun uploadProfile(profile: Profile, avatarUri: Uri?) {
        avatarUri?.let {
            val avatarRef = storageRef.child("avatars/${profile.email}.jpg")
            avatarRef.putFile(it).await()
            val avatarUrl = avatarRef.downloadUrl.await().toString()
            profile.avatarUrl = avatarUrl
        }
        profilesCollection.document(profile.email).set(profile).await()
    }

    suspend fun getProfileByEmail(email: String): Profile? {
        val document = profilesCollection.document(email).get().await()
        return document.toObject(Profile::class.java)
    }

    suspend fun downloadProfileImage(email: String): Uri? {
        val avatarRef = storageRef.child("avatars/$email.jpg")
        val localFile = File.createTempFile("avatar", "jpg")
        avatarRef.getFile(localFile).await()
        return Uri.fromFile(localFile)
    }
}