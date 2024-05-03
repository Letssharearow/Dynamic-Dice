package com.example.dynamicdiceprototype.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun <T> SelectItemsGrid(
    selectables: List<T>,
    size: Int,
    view: @Composable (item: T, modifier: Modifier) -> Unit,
    onSaveSelection: (Map<String, T>) -> Unit
) where T : Countable<T> {

  val selectedItems = remember { mutableStateMapOf<String, T>() }
  LazyVerticalGrid(columns = GridCells.Fixed(2)) {
    items(selectables) { item ->
      Box {
        val isSelected = selectables.contains(item)
        if (isSelected) {
          Icon(
              imageVector = Icons.Filled.Check,
              contentDescription = "Checked",
              modifier = Modifier.align(Alignment.TopStart))
        }
        view(item, Modifier.clickable { selectedItems[item.getId()] = item })
        if (isSelected) {
          Slider(
              value = item.getCount().toFloat(),
              onValueChange = { value ->
                selectedItems[item.getId()] =
                    selectedItems[item.getId()]!!.copy(count = value.toInt())
              },
              valueRange = 1f..size.toFloat(),
              steps = size,
              modifier =
                  Modifier.align(Alignment.BottomCenter)
                      .padding(8.dp)
                      .clip(RoundedCornerShape(12.dp))
                      .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5F)))
        }
      }
    }
  }
}
