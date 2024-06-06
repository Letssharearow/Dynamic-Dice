package com.example.dynamicdiceprototype.services

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

enum class PreferenceKey(val defaultValue: Any) {
  ItemSelectionMaxSize(100),
  ItemSelectionInitialSize(15),
  MenuDrawerMaxWidthFraction(75),
  SettingsHeader("Settings"),
  LastDiceGroup("Kniffel"),
  IsAddDiceCompact(true),
  IsDicesViewCompact(true),
  IsDicesGroupViewCompact(true),
}

object PreferenceManager {

  private lateinit var appContext: Context

  fun init(context: Context) {
    appContext = context.applicationContext
  }

  private fun getSharedPreferences(): SharedPreferences {
    check(::appContext.isInitialized) {
      "PreferenceManager is not initialized. Call init() with context first."
    }
    return appContext.getSharedPreferences("com.dynamic-dice.app", Context.MODE_PRIVATE)
  }

  fun <T> saveData(key: PreferenceKey, newValue: T) {
    val sharedPreferences = getSharedPreferences()
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

  @Suppress("UNCHECKED_CAST")
  fun <T> loadData(key: PreferenceKey): T {
    val sharedPreferences = getSharedPreferences()
    return when (key.defaultValue) {
      is Int -> sharedPreferences.getInt(key.name, key.defaultValue as Int) as T
      is String -> sharedPreferences.getString(key.name, key.defaultValue as String) as T
      is Boolean -> sharedPreferences.getBoolean(key.name, key.defaultValue as Boolean) as T
      else -> throw IllegalArgumentException("Unsupported type")
    }
  }

  fun <T> getPreferenceFlow(key: PreferenceKey): Flow<T> {
    val sharedPreferences = getSharedPreferences()
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
