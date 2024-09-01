package com.example.dynamicdiceprototype.services

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

enum class PreferenceKey(
    val defaultValue: Any,
    val uiName: String,
    val location: String = "No Steps provided"
) {
  ItemSelectionDiceGroupMaxSize(
      100, "Max Dice Groups", "Create a Dice Group and add more Dice to it"),
  ItemSelectionDiceWeightMaxSize(500, "Max Dice Weights", "Create a Die and max out it's weight"),
  ItemSelectionDiceValueMaxSize(5000, "Max Dice Values", "Create a Die and max out it's value"),
  CreateNumberedDiceMaxEndValue(
      100, "Numbered Die Max End Value", "Create a numbered Die and max out it's end value"),
  ItemSelectionInitialSize(
      15,
      "Inital Size on Item selection",
      "Create a Die and select anything and move slider all the way to the right"),
  MenuDrawerMaxWidthFraction(75, "Menu Drawer Max Width Fraction", "Open the Burger Menu"),
  ItemSelectionOneScreenGridMinWidth(
      400, "One Screen Grid Min Width", location = "Create a Die and Edit faces"),
  ItemSelctionDebounceTime(
      1000,
      "Debounce Time for Item Selection",
      location = "Create a Die, select face and slide all the way to the right"),
  SettingsHeader("Settings", "SettingsHeader", "the Title here"),
  IsDicesViewCompact(false, "Is Dice View Compact"),
  IsDicesGroupViewCompact(true, "Is Dice Groups View Compact"),
  HasOnboardingCompleted(false, "Has Onboarding Completed"),
} // TODO find better default value type solution to use default value when accessing it to set as

// initial state

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
