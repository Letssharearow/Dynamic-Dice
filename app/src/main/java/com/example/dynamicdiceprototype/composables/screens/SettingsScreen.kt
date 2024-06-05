package com.example.dynamicdiceprototype.composables.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.dynamicdiceprototype.services.PreferenceKey
import com.example.dynamicdiceprototype.services.PreferenceManager

@Composable
fun SettingsScreen() {
  val preferences = PreferenceKey.entries.toTypedArray()
  val headerText =
      PreferenceManager.getPreferenceFlow<String>( PreferenceKey.SettingsHeader)
          .collectAsState(initial = "Settings")
          .value

  Column(
      modifier =
          Modifier.fillMaxSize().padding(16.dp).background(MaterialTheme.colorScheme.background)) {
        Text(
            text = headerText,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp).align(Alignment.CenterHorizontally))

        preferences.forEach { preference ->
          val valueFlow = PreferenceManager.getPreferenceFlow<Any>( preference)
          val valueState = valueFlow.collectAsState(initial = preference.defaultValue)

          PreferenceItem(
              preference = preference,
              value = valueState.value,
              onValueChange = { newValue -> PreferenceManager.saveData( preference, newValue) })
        }
      }
}

@Composable
fun PreferenceItem(preference: PreferenceKey, value: Any, onValueChange: (Any) -> Unit) {
  when (value) {
    is Boolean -> {
      PreferenceSwitch(preference = preference, value = value, onValueChange = onValueChange)
    }
    is Int -> {
      PreferenceTextField(
          preference = preference,
          value = value.toString(),
          onValueChange = { newValue -> newValue.toIntOrNull()?.let { onValueChange(it) } },
          isNumeric = true)
    }
    is String -> {
      PreferenceTextField(
          preference = preference, value = value, onValueChange = onValueChange, isNumeric = false)
    }
  }
}

@Composable
fun PreferenceSwitch(preference: PreferenceKey, value: Boolean, onValueChange: (Boolean) -> Unit) {
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .padding(vertical = 8.dp)
              .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
              .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = preference.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge)
        Switch(
            checked = value,
            onCheckedChange = onValueChange,
            colors =
                SwitchDefaults.colors(
                    checkedThumbColor = Color.Green, uncheckedThumbColor = Color.Red))
      }
}

@Composable
fun PreferenceTextField(
    preference: PreferenceKey,
    value: String,
    onValueChange: (String) -> Unit,
    isNumeric: Boolean
) {
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .padding(vertical = 8.dp)
              .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
              .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = preference.name,
            modifier = Modifier.fillMaxWidth(0.33f),
            style = MaterialTheme.typography.bodyLarge)
        TextField(
            value = value,
            onValueChange = { newValue ->
              if (!isNumeric || newValue.all { it.isDigit() }) {
                onValueChange(newValue)
              }
            },
            keyboardOptions =
                if (isNumeric) {
                  KeyboardOptions(keyboardType = KeyboardType.Number)
                } else {
                  KeyboardOptions.Default
                },
            modifier =
                Modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp))
      }
}