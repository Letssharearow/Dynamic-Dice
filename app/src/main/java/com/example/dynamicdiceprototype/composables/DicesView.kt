package com.example.dynamicdiceprototype.composables

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.example.dynamicdiceprototype.data.Dice
import getMaxWidth

@Composable
fun DicesView(dices: List<Dice>, modifier: Modifier = Modifier) {
  BoxWithConstraints(modifier = modifier) {
    val density = LocalDensity.current
    val maxWidthPixels =
        getMaxWidth(dices.size, width = constraints.maxWidth, height = constraints.maxHeight)
    val maxWidthDp = with(density) { maxWidthPixels.toDp() }

    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = maxWidthDp)) {
      items(dices) { dice -> DiceView(dice = dice, size = maxWidthDp) }
    }
  }
}
