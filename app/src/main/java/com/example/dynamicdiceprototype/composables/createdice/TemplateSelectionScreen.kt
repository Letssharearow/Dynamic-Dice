package com.example.dynamicdiceprototype.composables.createdice

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dynamicdiceprototype.composables.ArrangedColumn
import com.example.dynamicdiceprototype.composables.ContinueButton
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.services.getDices
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateSelectionScreen(
    dices: List<Dice>,
    onSelectTemplate: (dice: Dice) -> Unit,
    onRemoveDice: (dice: Dice) -> Unit,
    onCreateNewDice: (number: Int) -> Unit
) {

  var isCompact by remember { mutableStateOf(false) }

  Column {
    // Toggle switch for isCompact
    Row(
        modifier = Modifier.fillMaxWidth().padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End) {
          Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.End) {
                Text("Toggle View", Modifier.padding(end = 8.dp))
                Switch(checked = isCompact, onCheckedChange = { isCompact = it })
              }
        }

    ArrangedColumn {
      LazyColumn {
        items(items = dices, key = { item -> item.name }) { template ->
          // Create a dismiss state for each item
          val dismissState =
              rememberDismissState(
                  confirmValueChange = { dismissValue ->
                    if (dismissValue == DismissValue.DismissedToStart) {
                      onRemoveDice(template)
                      true
                    } else {
                      false
                    }
                  })

          SwipeToDismiss(
              state = dismissState,
              directions = setOf(DismissDirection.EndToStart),
              background = {
                Box(Modifier.fillMaxSize()) {
                  Icon(
                      imageVector = Icons.Filled.Delete,
                      contentDescription = "Delete",
                      Modifier.align(Alignment.CenterEnd)
                          .size(80.dp)) // TODO remove fixed size and find better way
                }
              },
              dismissContent = {
                Box(modifier = Modifier.clickable { onSelectTemplate(template) }.fillMaxWidth()) {
                  DiceCard(template, isCompact)
                }
              })
        }
      }
      Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.wrapContentSize().padding(vertical = 16.dp)) {
            // Input field for numbers
            var number by remember { mutableStateOf<String?>("6") }
            OutlinedTextField(
                value = number.toString(),
                onValueChange = { newValue -> number = newValue.takeIf { it.isNotEmpty() } ?: "" },
                label = { Text("New Dice Faces Count") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.wrapContentSize(),
                isError = number.isNullOrEmpty())
            // Continue button
            ContinueButton(
                onClick = { onCreateNewDice(number?.toInt() ?: 0) }, // TODO Better handling?
                text = "+",
                enabled = !number.isNullOrEmpty())
          }
    }
  }
}

@Preview
@Composable
private fun preview() {
  DynamicDicePrototypeTheme {
    TemplateSelectionScreen(dices = getDices(4), onSelectTemplate = {}, onRemoveDice = {}) {
      //
    }
  }
}
