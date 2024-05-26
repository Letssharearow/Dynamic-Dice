package com.example.dynamicdiceprototype.services

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

enum class Preferences {
  IS_COMPACT,
  LAST_GROUP,
  MAX_SIZE,
}

enum class PreferenceView {
  Dice,
  Group,
  MainScreenAlertBox
}

enum class PreferenceKey(val defaultValue: Any) {
  MaxSize(100),
  InitialSize(15),
  SettingsHeader("Settings"),
  IsCompact(true),
}

class PreferenceManager(private val context: Context) {

  private val sharedPreferences: SharedPreferences by lazy {
    context.getSharedPreferences("com.dynamic-dice.app", Context.MODE_PRIVATE)
  }

  fun <T> saveData(key: PreferenceKey, newValue: T) {
    with(sharedPreferences.edit()) {
      when (newValue) {
        is Int -> putInt(key.name, newValue)
        is String -> putString(key.name, newValue)
        is Boolean -> putBoolean(key.name, newValue)
        else -> throw IllegalArgumentException("Unsupported type")
      }
      apply()
    }
  }

  fun <T> loadData(key: PreferenceKey): T {
    return when (key.defaultValue) {
      is Int -> sharedPreferences.getInt(key.name, key.defaultValue) as T
      is String -> sharedPreferences.getString(key.name, key.defaultValue) as T
      is Boolean -> sharedPreferences.getBoolean(key.name, key.defaultValue) as T
      else -> throw IllegalArgumentException("Unsupported type")
    }
  }

  fun <T> getPreferenceFlow(key: PreferenceKey): Flow<T> {
    return sharedPreferences.flowOf(key)
  }

  private fun <T> SharedPreferences.flowOf(key: PreferenceKey): Flow<T> {
    return callbackFlow {
      val listener =
          SharedPreferences.OnSharedPreferenceChangeListener { _, k ->
            if (k == key.name) {
              this.trySend(loadData(key)).isSuccess
            }
          }
      registerOnSharedPreferenceChangeListener(listener)
      trySend(loadData(key))
      awaitClose { unregisterOnSharedPreferenceChangeListener(listener) }
    }
  }
}

object PreferencesService {

  private fun getSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences("com.dynamic-dice.app", Context.MODE_PRIVATE)
  }

  fun saveData(context: Context, integer: Int) {
    val sharedPreferences = getSharedPreferences(context)
    with(sharedPreferences.edit()) {
      putInt("DARK_THEME_ENABLED", integer)
      apply()
    }
  }

  fun loadData(context: Context): Int {
    val sharedPreferences = getSharedPreferences(context)
    return sharedPreferences.getInt("DARK_THEME_ENABLED", 0) // false is the default value
  }

  fun loadIsCompact(
      context: Context,
      view: PreferenceView,
  ): Boolean {
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean(Preferences.IS_COMPACT.name.plus(view.name), false)
  }

  fun saveIsCompact(context: Context, isCompact: Boolean, view: PreferenceView) {
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
      putBoolean(Preferences.IS_COMPACT.name.plus(view.name), isCompact)
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
