package com.hcmus.data

import android.content.Context
import android.content.Context.MODE_PRIVATE


object ContextStore {
  fun get(context: Context, key: String): String? {
    return context.getSharedPreferences("super_safe", MODE_PRIVATE)
      .getString(key, null)
  }

  fun set(context: Context, key: String, value: String) {
    context.getSharedPreferences("super_safe", MODE_PRIVATE)
      .edit().putString(key, value).apply()
  }
}