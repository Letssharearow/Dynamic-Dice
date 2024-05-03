package com.example.dynamicdiceprototype.composables

import OneScreenGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.services.DiceViewModel

@Composable
fun DicesView(dices: List<Dice>, modifier: Modifier = Modifier) {
  val viewModel: DiceViewModel = viewModel()
  OneScreenGrid<Dice>(dices, minSize = 10F, modifier) { dice, maxSize ->
    DiceView(dice = dice, size = maxSize, onDiceClick = {viewModel.lockDice(it)})
  }
}
