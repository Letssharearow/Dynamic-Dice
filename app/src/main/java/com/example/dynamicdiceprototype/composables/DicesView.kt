package com.example.dynamicdiceprototype.composables

import OneScreenGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.dynamicdiceprototype.data.Dice

@Composable
fun DicesView(dices: List<Dice>, modifier: Modifier = Modifier) {
  OneScreenGrid<Dice>(dices, minSize = 10F, modifier) { dice, maxSize ->
    DiceView(dice = dice, size = maxSize, onDiceClick = {})
  }
}
