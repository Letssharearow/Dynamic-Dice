package com.example.dynamicdiceprototype.composables.screens.dice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.dynamicdiceprototype.composables.common.ItemListScreen
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.MenuItem
import com.example.dynamicdiceprototype.developer_area.getDices
import com.example.dynamicdiceprototype.services.PreferenceKey
import com.example.dynamicdiceprototype.services.PreferenceManager
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

@Composable
fun TemplateSelectionScreen(
    dices: List<Dice>,
    onSelectTemplate: (dice: Dice) -> Unit,
    menuActions: List<MenuItem<Dice>>,
    onCreateNewDice: () -> Unit,
    onCreateNumberedDice: (start: Int, end: Int) -> Unit,
    onCreateRandomDice: () -> Unit,
) {
  var showAlert by remember { mutableStateOf(false) }
  if (showAlert) {
    CreateNumberedDiceAlert(
        onCreateNumberedDice = onCreateNumberedDice, closeAlert = { showAlert = false })
  }

  ItemListScreen(
      items = dices.sortedBy { it.name.uppercase() },
      onSelect = onSelectTemplate,
      menuActions = menuActions,
      getKey = { it.id },
      preferenceView = PreferenceKey.IsDicesViewCompact,
      onCreateItem = onCreateNewDice,
      onSecondaryAction = { showAlert = true },
      onRandomItem = onCreateRandomDice) { item, isCompact, modifier ->
        DiceCard(item, isCompact, modifier)
      }
}

@Composable
fun CreateNumberedDiceAlert(
    onCreateNumberedDice: (start: Int, end: Int) -> Unit,
    closeAlert: () -> Unit,
    modifier: Modifier = Modifier
) {

  val maxEndValue =
      PreferenceManager.getPreferenceFlow<Int>(PreferenceKey.CreateNumberedDiceMaxEndValue)
          .collectAsState(initial = 100)
          .value

  var startValue by remember { mutableStateOf("1") }
  var endValue by remember { mutableStateOf("") }
  AlertDialog(
      onDismissRequest = closeAlert,
      title = { Text("Create Numbered dice") },
      text = {
        Column {
          Text("Enter the range for the numbered dice:")

          OutlinedTextField(
              value = startValue,
              onValueChange = { startValue = it },
              label = { Text("Start Value") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
          OutlinedTextField(
              value = endValue,
              onValueChange = { endValue = it },
              label = { Text("End Value") },
              isError = endValue.toIntOrNull()?.let { it > maxEndValue } ?: false,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        }
      },
      confirmButton = {
        Button(
            onClick = {
              val start = startValue.toIntOrNull()
              val end = endValue.toIntOrNull()
              if (start != null &&
                  end != null &&
                  start <= end &&
                  start > 0 &&
                  end > 0 &&
                  end <= maxEndValue) {
                onCreateNumberedDice(start, end)
                closeAlert()
              } else {
                // TODO show an error message or handle invalid input here
              }
            }) {
              Text("Create")
            }
      },
      dismissButton = { Button(onClick = closeAlert) { Text("Cancel") } })
}

@Preview
@Composable
private fun Preview() {
  DynamicDicePrototypeTheme {
    TemplateSelectionScreen(
        dices = getDices(4),
        onSelectTemplate = {},
        onCreateNewDice = {},
        onCreateRandomDice = {},
        onCreateNumberedDice = { _, _ -> },
        menuActions = listOf())
  }
}
