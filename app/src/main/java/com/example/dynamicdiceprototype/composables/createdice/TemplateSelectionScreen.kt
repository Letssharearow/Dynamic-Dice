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
    onCreateNewDice: () -> Unit,
    onCreateRandomDice: () -> Unit,
) {
  ItemListScreen(
      items = dices.sortedBy { it.name.uppercase() },
      onSelect = onSelectTemplate,
      menuActions = menuActions,
      getKey = { it.id },
      preferenceView = PreferenceKey.IsDicesViewCompact,
      onCreateItem = onCreateNewDice,
      onRandomItem = onCreateRandomDice) { item, isCompact, modifier ->
        DiceCard(item, isCompact, modifier)
      }
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
        menuActions = listOf())
  }
}
