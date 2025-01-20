package com.hcmus.ui.secret

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.hcmus.data.CloudFirestoreService
import com.hcmus.data.ContextStore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

object PinStorage {
  // mật khẩu là 4 số 1111
  private const val PREF_NAME = "pin_storage"
  private const val KEY_PIN = "user_pin"

  fun savePin(context: Context, pin: String) {
    val email = ContextStore.get(context, "email") ?: return
    Log.d("PinStorage", "Set pin: $pin")

    val db = CloudFirestoreService().db
    db.document(email).update("pin", pin)
//        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
//        prefs.edit().putString(KEY_PIN, pin).apply()
  }

  fun getPin(context: Context): String? = runBlocking {
//        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
//        return prefs.getString(KEY_PIN, null) // Trả về null nếu chưa có PIN
    val email = ContextStore.get(context, "email") ?: return@runBlocking null
    val db = CloudFirestoreService().db
    val docRef = db.document(email).get().await().get("pin")
    Log.d("PinStorage", "Get pin: $docRef")

    if (docRef == null) {
      return@runBlocking null
    }

    return@runBlocking docRef.toString()
  }
}
