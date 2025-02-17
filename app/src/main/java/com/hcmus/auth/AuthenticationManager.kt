package com.hcmus.auth

import android.content.Context
import android.util.Log
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.security.MessageDigest
import java.util.UUID

interface AuthResponse {
  data object Success : AuthResponse
  data class Error(val message: String) : AuthResponse
}

class AuthenticationManager(val context: Context) {
  private val auth = Firebase.auth

  fun createAccountWithEmail(email: String, password: String): Flow<AuthResponse> = callbackFlow {
    try {
      if (email.isEmpty() || password.isEmpty()) {
        trySend(AuthResponse.Error("Email and password cannot be empty"))
        return@callbackFlow
      }

      auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            Log.d("Login", "createAccountWithEmail Success")
            trySend(AuthResponse.Success)
          } else {
            Log.d("Login", "createAccountWithEmail Error: ${task.exception?.message}")
            trySend(AuthResponse.Error(task.exception?.message ?: "Unknown error"))
          }
        }
        .addOnFailureListener { e ->
          Log.e("Login", "createAccountWithEmail Failure: ${e.message}")
          trySend(AuthResponse.Error(e.message ?: "Failed to create account"))
        }
    } catch (e: Exception) {
      Log.e("Login", "createAccountWithEmail Exception: ${e.message}")
      trySend(AuthResponse.Error(e.message ?: "Unknown error occurred"))
    }

    awaitClose()
  }

  fun loginWithEmail(email: String, password: String): Flow<AuthResponse> = callbackFlow {
    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
      if (task.isSuccessful) {
        trySend(AuthResponse.Success)
      } else {
        Log.d("Login", "loginWithEmail Error: ${task.exception?.message}")
        trySend(AuthResponse.Error(task.exception?.message ?: "Unknown error"))
      }
    }
    awaitClose()
  }

  private fun createNonce(): String {
    val rawNonce = UUID.randomUUID().toString()
    val bytes = rawNonce.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold("") { str, it -> str + "%02x".format(it) }
  }

  fun signInWithGoogle(): Flow<AuthResponse> = callbackFlow {
    val googleIdOption = GetGoogleIdOption.Builder()
      .setFilterByAuthorizedAccounts(false)
      .setServerClientId("598947410794-r46llha05f4rtr0vfhuj1a7i0fp15qpt.apps.googleusercontent.com")
      .setAutoSelectEnabled(false)
      .setNonce(createNonce())
      .build()

    val request = GetCredentialRequest.Builder()
      .addCredentialOption(googleIdOption)
      .build()

    try {
      val credentialManager = CredentialManager.create(context)
      val response = credentialManager.getCredential(
        context = context,
        request = request
      )

      val credential = response.credential
      if (credential is CustomCredential) {
        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
          try {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val firebaseCredential = GoogleAuthProvider.getCredential(
              googleIdTokenCredential.idToken, null
            )

            auth.signInWithCredential(firebaseCredential).addOnCompleteListener {
              if (it.isSuccessful) {
                trySend(AuthResponse.Success)
              } else {
                trySend(AuthResponse.Error(message = it.exception?.message ?: ""))
              }
            }
          } catch (e: GoogleIdTokenParsingException) {
            trySend(AuthResponse.Error(message = e.message ?: ""))
          }
        }
      }
    } catch (e: Exception) {
      trySend(AuthResponse.Error(message = e.message ?: ""))
    }

    awaitClose()
  }
}