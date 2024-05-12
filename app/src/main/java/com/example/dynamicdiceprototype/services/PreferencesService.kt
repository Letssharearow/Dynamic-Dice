package com.example.dynamicdiceprototype.services

import android.content.Context

object PreferencesService {

  enum class Preferences {
    IS_COMPACT,
    LAST_GROUP
  }

  fun saveData(context: Context, integer: Int) {
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
      putInt("DARK_THEME_ENABLED", integer)
      apply()
    }
  }

  fun loadData(context: Context): Int {
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    return sharedPreferences.getInt("DARK_THEME_ENABLED", 0) // false is the default value
  }

  fun loadIsCompact(context: Context): Boolean {
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean(Preferences.IS_COMPACT.name, false)
  }

  fun saveIsCompact(context: Context, isCompact: Boolean) {
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
      putBoolean(Preferences.IS_COMPACT.name, isCompact)
      apply()
    }
  }

  fun loadLastBundle(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    return sharedPreferences.getString(
        Preferences.LAST_GROUP.name,
        "Kniffel")!! /*TODO resolve assert, this getString function doesnt return null, so why do I
                     need the assert?*/
  }

  fun saveLastBundle(context: Context, lastGroup: String) {
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
      putString(Preferences.LAST_GROUP.name, lastGroup)
      apply()
    }
  }
}