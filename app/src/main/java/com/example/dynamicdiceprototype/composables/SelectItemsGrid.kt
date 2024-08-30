package com.example.dynamicdiceprototype.composables

import OneScreenGrid
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.dynamicdiceprototype.services.PreferenceKey
import com.example.dynamicdiceprototype.services.PreferenceManager
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun <T> SelectItemsGrid(
    selectables: List<T>,
    onSaveSelection: (Map<T, Int>) -> Unit,
    getId: (T) -> String,
    modifier: Modifier = Modifier,
    initialSize: Int = 10,
    maxSize: Int = 100,
    showSlider: Boolean = true,
    showNumberCircle: Boolean = true,
    buttonText: String = "continue",
    applyFilter: ((T, String) -> Boolean)? = null,
    itemMinWidthPixel: Float? = null,
    initialValue: Map<T, Int> = mapOf(),
    handleItemClick: ((T) -> Unit)? = null,
    minValue: Int = 1,
    view: @Composable (item: T, modifier: Modifier, size: Dp, value: Int?) -> Unit,
) {
  val initialSizeUpdated =
      PreferenceManager.getPreferenceFlow<Int>(PreferenceKey.ItemSelectionInitialSize)
          .collectAsState(initial = initialSize)
          .value
  val debounceDelay =
      PreferenceManager.getPreferenceFlow<Int>(PreferenceKey.ItemSelctionDebounceTime)
          .collectAsState(initial = 1000)
          .value
  val oneScreenGridMinWidth =
      itemMinWidthPixel
          ?: PreferenceManager.getPreferenceFlow<Int>(
                  PreferenceKey.ItemSelectionOneScreenGridMinWidth)
              .collectAsState(initial = 400)
              .value

  var selectablesFiltered by remember { mutableStateOf(selectables) }
  val selectedItems = remember {
    mutableStateMapOf(*initialValue.map { it.toPair() }.toTypedArray())
  }
  var filter by remember { mutableStateOf("") }
  val sumOfSelection = selectedItems.values.sum()

  LaunchedEffect(applyFilter, selectables, filter) {
    selectablesFiltered =
        if (applyFilter != null) selectables.filter { applyFilter(it, filter) } else selectables
  }

  ArrangedColumn(modifier = modifier) {
    FilterInput(applyFilter, filter) { newFilter -> filter = newFilter }

    OneScreenGrid(
        items = selectablesFiltered,
        minSize = oneScreenGridMinWidth.toFloat(),
        modifier = Modifier.weight(1f)) { item, maxWidthDp ->
          Box(
              contentAlignment = Alignment.Center,
              modifier =
                  Modifier.fillMaxWidth().padding(8.dp).clickable {
                    val selectedItem = selectedItems[item]
                    if (selectedItem == null) {
                      if (sumOfSelection < maxSize) {
                        selectedItems[item] = minValue
                        if (handleItemClick != null) {
                          handleItemClick(item)
                        }
                      }
                    } else {
                      selectedItems.remove(item)
                    }
                  }) {
                val selectedItem = selectedItems[item]

                view(item, Modifier, maxWidthDp, selectedItem)
                selectedItem?.let {
                  if (showNumberCircle) {
                    NumberCircle(
                        text = selectedItem.toString(),
                        fontSize = 24.sp,
                        modifier = Modifier.align(Alignment.TopStart))
                  }
                }
                Box(
                    Modifier.align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.66f))
                        .padding(8.dp)) {
                      selectedItem?.let {
                        var debounceJob by remember { mutableStateOf<Job?>(null) }
                        var mutableSize by remember {
                          mutableIntStateOf(
                              initialSizeUpdated.coerceAtMost(maxSize - sumOfSelection + it))
                        }

                        if (showSlider) {
                          Slider(
                              value = it.toFloat(),
                              onValueChange = { value ->
                                val selectedCurrentItem = selectedItems[item]
                                selectedCurrentItem?.let { currentCount ->
                                  val newCount = value.toInt()
                                  val newSum = sumOfSelection - currentCount + newCount
                                  if (newSum <= maxSize) {
                                    selectedItems[item] = newCount
                                  }
                                  debounceJob?.cancel()
                                  debounceJob =
                                      GlobalScope.launch {
                                        delay(debounceDelay.toLong())
                                        mutableSize =
                                            when {
                                              newCount == mutableSize -> {
                                                (mutableSize * 2).coerceAtMost(
                                                    maxSize - sumOfSelection + currentCount)
                                              }
                                              newCount < initialSizeUpdated -> initialSizeUpdated
                                              else -> mutableSize
                                            }
                                      }
                                }
                              },
                              valueRange = 1f..mutableSize.toFloat().coerceAtLeast(1f),
                              steps = (mutableSize - minValue).coerceAtLeast(minValue))
                        }
                      }
                      if (!showSlider || selectedItem == null) {
                        Text(
                            text = getId(item),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.Center))
                      }
                    }
              }
        }
    ContinueButton(
        onClick = { onSaveSelection(selectedItems) }, text = "$buttonText ($sumOfSelection)")
  }
}

@Composable
private fun <T> FilterInput(
    applyFilter: ((T, String) -> Boolean)?,
    filter: String,
    onFilterChange: (String) -> Unit
) {
  applyFilter?.let { _ ->
    SingleLineTextInput(
        text = filter,
        onValueChange = onFilterChange,
        label = "Filter",
        isError = false,
        modifier = Modifier.padding(horizontal = 8.dp))
  }
}

@Preview(showBackground = true, device = Devices.PIXEL_TABLET)
@Composable
private fun SelectItemsGridPreview() {
  DynamicDicePrototypeTheme {
    SelectItemsGrid<Dice>(
        selectables = listOf(Dice(name = "testDiceCheck", faces = listOf())),
        onSaveSelection = {},
        getId = { dice -> dice.name },
        initialSize = 2,
        initialValue = mutableMapOf(Dice() to 4),
        view = { item, modifier, maxWidthDp, value ->
          DiceView(dice = item, size = maxWidthDp, modifier = modifier.fillMaxSize())
        },
        minValue = 1)
  }
}
