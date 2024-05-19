package com.example.dynamicdiceprototype.composables.screens

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.example.dynamicdiceprototype.composables.SelectItemsGrid
import com.example.dynamicdiceprototype.composables.SingleLineInput
import com.example.dynamicdiceprototype.composables.common.ArrangedColumn
import com.example.dynamicdiceprototype.composables.createdice.DicePreview
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

@Composable
fun DiceGroupCreationScreen(
    dices: List<Pair<Dice, Int>>,
    onCreateDiceGroup: (name: String, dices: Map<String, Pair<Dice, Int>>) -> Unit,
    isEdit: Boolean,
    groupSize: Int = 4,
    initialValue: Map<String, Pair<Dice, Int>>? = mapOf(),
    initialName: String = "Change Later",
) {
  var name by remember { mutableStateOf(initialName) }
  var number by remember { mutableStateOf<String?>("$groupSize") }
  ArrangedColumn {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max)) {
          SingleLineInput(
              text = name,
              onValueChange = { name = it },
              label = "Dice Name",
              isReadOnly = isEdit,
              modifier = Modifier.padding(8.dp).fillMaxWidth(0.5F))

          OutlinedTextField(
              value = number.toString(),
              onValueChange = { newValue ->
                number =
                    newValue.takeIf {
                      it.isDigitsOnly() && (it.isNotEmpty() && it.toInt() <= 100 || it.isEmpty())
                    } ?: number
              },
              singleLine = true,
              label = { Text("New Dice Faces Count") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier.wrapContentSize(),
              isError = number.isNullOrEmpty())
        }

    SelectItemsGrid(
        selectables = dices,
        size = number?.takeIf { it.isDigitsOnly() && it.isNotEmpty() }?.toInt() ?: 0,
        onSaveSelection = { onCreateDiceGroup(name, it) },
        initialValue = initialValue ?: mapOf(),
        getCount = { it.second },
        getId = { it.first.name },
        copy = { diceAndCount, count -> Pair(diceAndCount.first, count) }) {
            diceAndCount,
            modifier,
            maxWidth ->
          DicePreview(
              dice = diceAndCount.first,
              facesSum = diceAndCount.first.faces.sumOf { it.weight },
              Modifier.size(maxWidth))
        }
  }
}

@Composable
@Preview(showSystemUi = true, device = Devices.PIXEL_TABLET)
fun DiceroupsScreenPreview() {
  DynamicDicePrototypeTheme {
    val dices = listOf(Dice("checked"), Dice("Name"), Dice("Name"))
    DiceGroupCreationScreen(
        dices = dices.map { Pair(it, 0) },
        onCreateDiceGroup = { string, map -> },
        groupSize = 20,
        isEdit = false,
        initialValue = mapOf("checked" to Pair(first = Dice(), second = 3)))
  }
}
