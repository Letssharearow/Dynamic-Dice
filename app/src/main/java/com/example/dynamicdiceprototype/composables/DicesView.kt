package com.example.dynamicdiceprototype.composables

import OneScreenGrid
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.services.DiceViewModel
import com.example.dynamicdiceprototype.services.getDices
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

@Composable
fun DicesView(dices: List<Dice>, modifier: Modifier = Modifier) {
  val inPreviewMode = LocalInspectionMode.current
  val viewModel: DiceViewModel? = if (inPreviewMode) null else viewModel()
  OneScreenGrid<Dice>(dices, minSize = 10F, modifier) { dice, maxSize ->
    DiceView(dice = dice, size = maxSize, onDiceClick = {viewModel?.lockDice(it)})
  }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
  DynamicDicePrototypeTheme {
    Box(Modifier.height(800.dp).width(300.dp)) {
      Column {
        Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(Color.Cyan))
        DicesView(dices = getDices(8), Modifier.weight(1f))

        Box(modifier = Modifier.fillMaxWidth().height(40.dp).background(Color.Cyan))
      }
    }
  }
}