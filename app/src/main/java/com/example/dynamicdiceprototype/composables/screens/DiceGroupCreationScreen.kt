package com.example.dynamicdiceprototype.composables.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dynamicdiceprototype.Exceptions.DiceNotFoundException
import com.example.dynamicdiceprototype.composables.SelectItemsGrid
import com.example.dynamicdiceprototype.composables.SingleLineTextInput
import com.example.dynamicdiceprototype.composables.common.ArrangedColumn
import com.example.dynamicdiceprototype.composables.createdice.DicePreview
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.DiceGroup
import com.example.dynamicdiceprototype.services.PreferenceManager
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

@Composable
fun DiceGroupCreationScreen(
    dices: List<Dice>,
    onSaveSelection: (name: String, dices: Map<Dice, Int>) -> Unit,
    initialValue: DiceGroup? = null,
) {
  var name by remember { mutableStateOf(initialValue?.name ?: "Change Later") }
  ArrangedColumn {
    SingleLineTextInput(
        text = name,
        onValueChange = { name = it },
        label = "Dice Group Name",
        isReadOnly = false,
        modifier = Modifier.padding(8.dp))

    // TODO Improve so that the user can create a sorting for the dices (e.g. they are sorted the
    // way they were selected)
    SelectItemsGrid<Dice>(
        selectables = dices,
        onSaveSelection = { if (name.isNotEmpty()) onSaveSelection(name, it) },
        getId = { it.name },
        initialValue = transformDiceGroup(initialValue, dices),
    ) { dice, modifier, maxWidth, _ ->
      DicePreview(dice = dice, facesSum = dice.faces.sumOf { it.weight }, Modifier.size(maxWidth))
    }
  }
}

fun transformDiceGroup(diceGroupPair: DiceGroup?, dices: List<Dice>): Map<Dice, Int> {
  val diceGroup = diceGroupPair ?: return emptyMap()
  val diceMap = mutableMapOf<Dice, Int>()

  for ((diceId, count) in diceGroup.dices) {
    val dice =
        dices.find { it.id == diceId }
            ?: throw DiceNotFoundException(
                "Dice with id $diceId could not be found in the dices List $dices")
    diceMap[dice] = count
  }

  return diceMap.toMap()
}

@Composable
@Preview(showSystemUi = true, device = Devices.PIXEL_TABLET)
fun DiceroupsScreenPreview() {
  DynamicDicePrototypeTheme {
    val context = LocalContext.current
    PreferenceManager.init(context)
    val dices = listOf(Dice("checked"), Dice("Name"), Dice("Name"))
    DiceGroupCreationScreen(dices = dices, onSaveSelection = { string, map -> })
  }
}
