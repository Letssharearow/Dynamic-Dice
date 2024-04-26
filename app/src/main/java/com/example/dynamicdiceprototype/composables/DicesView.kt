package com.example.dynamicdiceprototype.composables

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.example.dynamicdiceprototype.data.Dice
import getMaxWidth

@Composable
fun DicesViewTest(dices: List<Dice>, modifier: Modifier = Modifier) {
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

@Composable
fun <T> OneScreenGrid(
    items: List<T>,
    minSize: Float, // TODO use itemLimit
    modifier: Modifier = Modifier,
    onRender: @Composable (item: T, maxWidth: Dp) -> Unit,
) {
  BoxWithConstraints(modifier = modifier) {
    val density = LocalDensity.current
    val maxWidthPixels =
        getMaxWidth(items.size, width = constraints.maxWidth, height = constraints.maxHeight)
    val MaxSize = Math.max(minSize, maxWidthPixels)
    val maxWidthDp = with(density) { MaxSize.toDp() }

    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = maxWidthDp)) {
      items(items) { item -> onRender(item, maxWidthDp) }
    }
  }
}

@Composable
fun DicesView(dices: List<Dice>, modifier: Modifier = Modifier) {
  OneScreenGrid<Dice>(dices, minSize = 10F, modifier) { dice, maxSize -> DiceView(dice, maxSize) }
}
