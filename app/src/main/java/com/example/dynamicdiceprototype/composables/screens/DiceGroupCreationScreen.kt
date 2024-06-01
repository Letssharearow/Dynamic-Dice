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
import com.example.dynamicdiceprototype.composables.SelectItemsGrid
import com.example.dynamicdiceprototype.composables.SingleLineTextInput
import com.example.dynamicdiceprototype.composables.common.ArrangedColumn
import com.example.dynamicdiceprototype.composables.createdice.DicePreview
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.services.PreferenceManager
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

@Composable
fun DiceGroupCreationScreen(
    dices: List<Dice>,
    onSaveSelection: (name: String, dices: Map<Dice, Int>) -> Unit,
    isEdit: Boolean,
    initialValue: Map<Dice, Int>? = mapOf(),
    initialName: String = "Change Later",
) {
  var name by remember { mutableStateOf(initialName) }
  ArrangedColumn {
    SingleLineTextInput(
        text = name,
        onValueChange = { name = it },
        label = "Dice Name",
        isReadOnly = isEdit,
        modifier = Modifier.padding(8.dp))

    SelectItemsGrid<Dice>(
        selectables = dices,
        onSaveSelection = { if (name.isNotEmpty()) onSaveSelection(name, it) },
        getId = { it.name },
        initialValue = initialValue ?: mapOf()) { dice, modifier, maxWidth ->
          DicePreview(
              dice = dice, facesSum = dice.faces.sumOf { it.weight }, Modifier.size(maxWidth))
        }
  }
}

@Composable
@Preview(showSystemUi = true, device = Devices.PIXEL_TABLET)
fun DiceroupsScreenPreview() {
  DynamicDicePrototypeTheme {
    val context = LocalContext.current
    PreferenceManager.init(context)
    val dices = listOf(Dice("checked"), Dice("Name"), Dice("Name"))
    DiceGroupCreationScreen(
        dices = dices,
        onSaveSelection = { string, map -> },
        isEdit = false,
        initialValue = mapOf(Dice("checked") to 3))
  }
}
