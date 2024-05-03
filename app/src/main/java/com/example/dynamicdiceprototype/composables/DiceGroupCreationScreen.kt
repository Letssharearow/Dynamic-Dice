package com.example.dynamicdiceprototype.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.services.getDices
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

@Composable
fun DiceGroupCreationScreen(
    dices: List<Dice>,
    onCreateDiceGroup: (diceGroup: Map<String, List<Dice>>) -> Unit,
) {
  var selectedDices = remember { mutableStateListOf<Dice>() }
}

@Preview(showSystemUi = true)
@Composable
private fun DiceGroupsScreenPreview() {
  DynamicDicePrototypeTheme { DiceGroupCreationScreen(getDices(10)) {} }
}
