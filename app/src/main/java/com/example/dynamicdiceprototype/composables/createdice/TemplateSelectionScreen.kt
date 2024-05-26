package com.example.dynamicdiceprototype.composables.createdice

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.dynamicdiceprototype.composables.ItemListScreen
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.MenuItem
import com.example.dynamicdiceprototype.services.PreferenceKey
import com.example.dynamicdiceprototype.services.getDices
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

@Composable
fun TemplateSelectionScreen(
    dices: List<Dice>,
    onSelectTemplate: (dice: Dice) -> Unit,
    menuActions: List<MenuItem<Dice>>,
    onCreateNewDice: () -> Unit
) {
  ItemListScreen(
      items = dices,
      onSelect = onSelectTemplate,
      menuActions = menuActions,
      getKey = { it.name },
      preferenceView = PreferenceKey.IsDicesViewCompact,
      onCreateItem = onCreateNewDice) { item, isCompact, modifier ->
        DiceCard(item, isCompact, modifier)
      }
}

@Preview
@Composable
private fun Preview() {
  DynamicDicePrototypeTheme {
    TemplateSelectionScreen(dices = getDices(4), onSelectTemplate = {}, menuActions = listOf()) {
      //
    }
  }
}
