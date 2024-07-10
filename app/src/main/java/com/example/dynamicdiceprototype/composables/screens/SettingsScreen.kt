package com.example.dynamicdiceprototype.composables.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults.rememberPlainTooltipPositionProvider
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.dynamicdiceprototype.services.HeaderViewModel
import com.example.dynamicdiceprototype.services.PreferenceKey
import com.example.dynamicdiceprototype.services.PreferenceManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
  val preferences = PreferenceKey.entries.toTypedArray()
  val headerText =
      PreferenceManager.getPreferenceFlow<String>(PreferenceKey.SettingsHeader)
          .collectAsState(initial = "Settings")
          .value
  LaunchedEffect(headerText) { HeaderViewModel.changeHeaderText(headerText) }

  LazyColumn(
      modifier =
          Modifier.fillMaxSize().padding(16.dp).background(MaterialTheme.colorScheme.background)) {
        items(preferences, key = { it.name }) { preference ->
          val valueFlow = PreferenceManager.getPreferenceFlow<Any>(preference)
          val valueState = valueFlow.collectAsState(initial = preference.defaultValue)

          PreferenceItem(
              preference = preference,
              value = valueState.value,
              onValueChange = { newValue -> PreferenceManager.saveData(preference, newValue) },
              onResetValue = { PreferenceManager.saveData(preference, preference.defaultValue) },
          )
        }
      }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceText(preference: PreferenceKey) {

  val tooltipState = rememberTooltipState(isPersistent = true)
  val scope = rememberCoroutineScope()

  TooltipBox(
      positionProvider = rememberPlainTooltipPositionProvider(),
      tooltip = {
        PlainTooltip {
          Text(
              text =
                  "you can see changes made to this property when you do the following: ${preference.location} \n\ndefault value: ${preference.defaultValue}")
        }
      },
      state = tooltipState) {
        Text(
            text = preference.name,
            modifier =
                Modifier.fillMaxWidth(0.33f).clickable { scope.launch { tooltipState.show() } },
            style = MaterialTheme.typography.bodyLarge)
      }
}

@Composable
fun PreferenceItem(
    preference: PreferenceKey,
    value: Any,
    onValueChange: (Any) -> Unit,
    onResetValue: () -> Unit
) {

  Row(
      modifier =
          Modifier.fillMaxWidth()
              .padding(vertical = 8.dp)
              .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
              .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically) {
        PreferenceText(preference)
        Box(modifier = Modifier.weight(1f)) {
          when (value) {
            is Boolean -> {
              PreferenceSwitch(value = value, onValueChange = onValueChange)
            }
            is Int -> {
              PreferenceTextField(
                  value = if (value == 0) "" else value.toString(),
                  onValueChange = { newValue -> onValueChange(newValue.toIntOrNull() ?: 0) },
                  isNumeric = true)
            }
            is String -> {
              PreferenceTextField(value = value, onValueChange = onValueChange, isNumeric = false)
            }
          }
        }
        IconButton(onClick = onResetValue) {
          Icon(imageVector = Icons.Default.Refresh, contentDescription = "reset value Button")
        }
      }
}

@Composable
fun PreferenceSwitch(value: Boolean, onValueChange: (Boolean) -> Unit) {
  Switch(
      checked = value,
      onCheckedChange = onValueChange,
      colors =
          SwitchDefaults.colors(checkedThumbColor = Color.Green, uncheckedThumbColor = Color.Red))
}

@Composable
fun PreferenceTextField(value: String, onValueChange: (String) -> Unit, isNumeric: Boolean) {
  TextField(
      value = value,
      onValueChange = { newValue ->
        if (!isNumeric || newValue.all { it.isDigit() }) {
          onValueChange(newValue)
        }
      },
      singleLine = true,
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
