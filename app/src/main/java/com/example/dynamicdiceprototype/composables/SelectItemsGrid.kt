package com.example.dynamicdiceprototype.composables

import OneScreenGrid
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dynamicdiceprototype.composables.common.ArrangedColumn
import com.example.dynamicdiceprototype.composables.common.ContinueButton
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.DiceInGroup
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme
import kotlin.math.ceil

@Composable
fun <T> SelectItemsGrid(
    selectables: List<T>,
    initialValue: Map<String, T> = mapOf(),
    size: Int,
    onSaveSelection: (Map<String, T>) -> Unit,
    getCount: (T) -> Int,
    getId: (T) -> String,
    copy: (item: T, count: Int) -> T,
    modifier: Modifier = Modifier,
    view: @Composable (item: T, modifier: Modifier, size: Dp) -> Unit,
) {

  val selectedItems = remember {
    mutableStateMapOf<String, T>(*initialValue.map { Pair(it.key, it.value) }.toTypedArray())
  }
  ArrangedColumn {
    OneScreenGrid(items = selectables, minSize = 400f, modifier.weight(1f)) { item, maxWidthDp
      -> // TODO hardcoded minSize, maybe as parameter?
      Box(
          contentAlignment = Alignment.Center,
          modifier =
              Modifier.fillMaxWidth().padding(8.dp).clickable {
                val selectedItem = selectedItems[getId(item)]
                if (selectedItem == null) {
                  selectedItems[getId(item)] = item
                  copy(item, 1)
                } else {
                  copy(item, 0)
                  selectedItems.remove(getId(item))
                }
              }) {
            val isSelected = selectedItems[getId(item)] != null
            if (isSelected) {
              Icon(
                  imageVector = Icons.Filled.Check,
                  contentDescription = "Checked",
                  modifier = Modifier.align(Alignment.TopStart))
            }
            view(item, Modifier, maxWidthDp)
            if (isSelected) {
              val selectedItem = selectedItems[getId(item)]!!
              Slider(
                  value = getCount(selectedItem).toFloat(),
                  onValueChange = { value ->
                    selectedItems[getId(item)] =
                        copy(selectedItems[getId(item)]!!, ceil(value).toInt())
                  },
                  valueRange = 1f..size.toFloat().coerceAtLeast(1f),
                  steps = (size - 1).coerceAtLeast(1),
                  modifier =
                      Modifier.align(Alignment.BottomCenter)
                          .padding(8.dp)
                          .clip(RoundedCornerShape(12.dp))
                          .background(
                              MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5F)))
            } else {
              Text(
                  text = getId(item),
                  modifier =
                      Modifier.align(Alignment.BottomCenter)
                          .padding(8.dp)
                          .clip(RoundedCornerShape(12.dp))
                          .background(
                              MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5F)))
            }
          }
    }
    ContinueButton(
        onClick = { onSaveSelection(selectedItems) },
        text = "Save Selection  : (${selectedItems.values.sumOf {getCount(it)}} / $size)")
  }
}

@Preview(showBackground = true, device = Devices.PIXEL_TABLET)
@Composable
private fun SelectItemsGridPreview() {
  DynamicDicePrototypeTheme {
    SelectItemsGrid<DiceInGroup>(
        selectables =
            listOf(
                DiceInGroup(Dice(name = "testDiceCheck", faces = listOf()), count = 2),
                DiceInGroup(Dice(name = "testDiceCheckAndName", faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
                DiceInGroup(Dice(faces = listOf()), count = 2),
            ),
        size = 2,
        onSaveSelection = {},
        initialValue =
            mutableMapOf(
                "testDiceCheck" to DiceInGroup(Dice(), 2),
                "testDiceCheckAndName" to DiceInGroup(Dice(), 0)),
        getCount = { diceWithCount -> diceWithCount.count },
        copy = { diceWithCount, count -> diceWithCount.copy(count = count) },
        getId = { diceWithCount -> diceWithCount.dice.name }) { item, modifier, maxWidthDp ->
          DiceView(dice = item.dice, size = maxWidthDp, modifier = modifier.fillMaxSize())
        }
  }
}
