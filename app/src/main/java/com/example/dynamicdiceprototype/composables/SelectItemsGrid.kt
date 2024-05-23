package com.example.dynamicdiceprototype.composables

import OneScreenGrid
import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dynamicdiceprototype.composables.common.ArrangedColumn
import com.example.dynamicdiceprototype.composables.common.ContinueButton
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.DiceInGroup
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme
import kotlin.math.ceil

@Composable
fun <T> SelectItemsGrid(
    selectables: List<T>,
    onSaveSelection: (Map<String, T>) -> Unit,
    getCount: (T) -> Int,
    getId: (T) -> String,
    copy: (item: T, count: Int) -> T,
    modifier: Modifier = Modifier,
    initialSize: Int = 10,
    maxSize: Int = 100,
    initialValue: Map<String, T> = mapOf(),
    isFilterable: Boolean = false,
    view: @Composable (item: T, modifier: Modifier, size: Dp) -> Unit,
) {

  var selectablesFiltered by remember { mutableStateOf(selectables) }

  val selectedItems = remember {
    mutableStateMapOf<String, T>(*initialValue.map { Pair(it.key, it.value) }.toTypedArray())
  }
  var filter by remember { mutableStateOf("") }
  val sumOfSelection = selectedItems.values.sumOf { getCount(it) }

  LaunchedEffect(selectables, filter) {
    selectablesFiltered =
        selectables.filter { item ->
          filter.isEmpty() || getId(item).contains(filter, ignoreCase = true)
        }
  }

  ArrangedColumn(modifier = Modifier.padding(4.dp)) {
    if (isFilterable) {
      SingleLineInput(
          text = filter,
          onValueChange = { value ->
            filter = value
            selectablesFiltered =
                selectables.filter { item ->
                  filter.isEmpty() || getId(item).contains(filter, ignoreCase = true)
                }
          },
          label = "Filter")
    }
    OneScreenGrid(items = selectablesFiltered, minSize = 400f, modifier.weight(1f)) {
        item,
        maxWidthDp -> // TODO hardcoded minSize, maybe as parameter?
      var mutableSize by remember { mutableIntStateOf(initialSize) }
      Box(
          contentAlignment = Alignment.Center,
          modifier =
              Modifier.fillMaxWidth().padding(8.dp).clickable {
                val selectedItem = selectedItems[getId(item)]
                if (selectedItem == null) {
                  selectedItems[getId(item)] = copy(item, 1)
                } else {
                  selectedItems.remove(getId(item))
                }
              }) {
            val selectedItem = selectedItems[getId(item)]

            view(item, Modifier, maxWidthDp)
            selectedItem?.let {
              NumberCircle(
                  text = getCount(it).toString(),
                  fontSize = 24.sp,
                  modifier = Modifier.align(Alignment.TopStart))
            }
            Box(
                Modifier.align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.66f))
                    .padding(8.dp)) {
                  selectedItem?.let {
                    var lastTimeClicked by remember { mutableLongStateOf(0L) }

                    Slider(
                        value = getCount(it).toFloat(),
                        onValueChange = { value ->
                          selectedItems[getId(item)] = copy(it, ceil(value).toInt())
                          val now = SystemClock.uptimeMillis()
                          if (now - lastTimeClicked > 2000) {
                            mutableSize =
                                when {
                                  getCount(it) == mutableSize ->
                                      (mutableSize * 2).coerceAtMost(
                                          maxSize - (sumOfSelection - mutableSize))
                                  getCount(it) < initialSize -> initialSize
                                  else -> mutableSize
                                }
                          }
                          lastTimeClicked = now
                        },
                        valueRange = 1f..mutableSize.toFloat().coerceAtLeast(1f),
                        steps = (mutableSize - 1).coerceAtLeast(1))
                  }
                      ?: Text(
                          text = getId(item),
                          style = MaterialTheme.typography.bodyLarge,
                          modifier = Modifier.align(Alignment.Center))
                }
          }
    }
    ContinueButton(
        onClick = { onSaveSelection(selectedItems) },
        text = "Save Selection  : ($sumOfSelection / $initialSize)")
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
        initialSize = 2,
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
