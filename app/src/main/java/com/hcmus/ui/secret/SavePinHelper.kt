package com.hcmus.ui.secret

import android.content.Context
import android.content.SharedPreferences

object PinStorage {// mật khẩu là 4 số 1111
    private const val PREF_NAME = "pin_storage"
    private const val KEY_PIN = "user_pin"

    fun savePin(context: Context, pin: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_PIN, pin).apply()
    }

    fun getPin(context: Context): String? {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_PIN, null) // Trả về null nếu chưa có PIN
    }
}
