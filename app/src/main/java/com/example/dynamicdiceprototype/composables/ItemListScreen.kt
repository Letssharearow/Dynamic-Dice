package com.example.dynamicdiceprototype.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.example.dynamicdiceprototype.composables.common.ArrangedColumn
import com.example.dynamicdiceprototype.composables.common.ContinueButton
import com.example.dynamicdiceprototype.composables.createdice.DiceCard
import com.example.dynamicdiceprototype.composables.screens.DiceGroupItem
import com.example.dynamicdiceprototype.data.AlterBoxProperties
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.MenuItem
import com.example.dynamicdiceprototype.services.PreferenceView
import com.example.dynamicdiceprototype.services.PreferencesService
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

@Composable
fun <T> PupMenuWithAlert(
    actionItem: T,
    items: List<MenuItem<T>>,
    showMenu: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {

  var alertProps by remember { mutableStateOf<Pair<(T) -> Unit, AlterBoxProperties>?>(null) }

  DropdownMenu(expanded = showMenu, onDismissRequest = { onDismiss() }, modifier = modifier) {
    items.forEach { item ->
      DropdownMenuItem(
          text = { Text(text = item.text) },
          onClick = {
            onDismiss()
            if (item.alert != null) {
              alertProps = Pair(item.callBack, item.alert)
            } else {
              item.callBack(actionItem)
            }
          })
    }
  }
  alertProps?.let {
    AlertDialog(
        onDismissRequest = { alertProps = null },
        title = { Text(it.second.header) },
        text = { Text(it.second.description) },
        confirmButton = { Button(onClick = { it.first(actionItem) }) { Text("Confirm") } },
        dismissButton = { Button(onClick = { alertProps = null }) { Text("Cancel") } })
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun <T> ItemListScreen(
    items: List<T>,
    onSelect: (item: T) -> Unit,
    menuActions: List<MenuItem<T>>,
    getKey: (item: T) -> String,
    onCreateItem: (amount: Int) -> Unit,
    modifier: Modifier = Modifier,
    preferenceView: PreferenceView = PreferenceView.Dice,
    view: @Composable (item: T, isCompact: Boolean, modifier: Modifier) -> Unit,
) {

  val preferencesService: PreferencesService = PreferencesService
  val context = LocalContext.current
  var isCompact by remember {
    mutableStateOf(preferencesService.loadIsCompact(context, preferenceView))
  }

  Column(modifier.padding(8.dp)) {
    // Toggle switch for isCompact
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End) {
          Text("Toggle View", Modifier.padding(end = 8.dp))
          Switch(
              checked = isCompact,
              onCheckedChange = {
                isCompact = it
                preferencesService.saveIsCompact(context, isCompact, preferenceView)
              })
        }

    ArrangedColumn(Modifier.weight(1f)) {
      LazyColumn {
        items(items = items, key = getKey) { item ->
          var showMenu by remember { mutableStateOf(false) }
          Box(
              modifier =
                  Modifier.combinedClickable(
                          onClick = { onSelect(item) }, onLongClick = { showMenu = true })
                      .fillMaxWidth()
                      .background(
                          shape = RoundedCornerShape(16.dp), // TODO reuse for background
                          color = MaterialTheme.colorScheme.background)) {
                view(item, isCompact, Modifier.fillMaxSize())

                Box(Modifier.align(Alignment.BottomEnd)) {
                  PupMenuWithAlert(
                      actionItem = item,
                      items = menuActions,
                      showMenu = showMenu,
                      onDismiss = { showMenu = false },
                  )
                }
              }
        }
      }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.wrapContentSize().padding(top = 8.dp)) {
          // Input field for numbers
          var number by remember { mutableStateOf<String?>("6") }
          OutlinedTextField(
              value = number.toString(),
              onValueChange = { newValue ->
                number =
                    newValue.takeIf {
                      it.isDigitsOnly() && (it.isNotEmpty() && it.toInt() <= 100 || it.isEmpty())
                    } ?: number
              },
              label = { Text("Count") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier.wrapContentSize(),
              isError = number.isNullOrEmpty())
          // Continue button
          ContinueButton(
              onClick = { onCreateItem(number?.toInt() ?: 0) }, // TODO Better handling?
              text = "+",
              enabled = !number.isNullOrEmpty())
        }
  }
}

@Preview
@Composable
private fun Preview() {
  DynamicDicePrototypeTheme {
    ItemListScreen(
        items = listOf(Dice(name = "test"), Dice(name = "test2"), Dice(name = "test3")),
        onSelect = {},
        menuActions = listOf(),
        getKey = { it.name },
        onCreateItem = {}) { item, isCompact, modifier ->
          DiceCard(dice = item, isCompact = isCompact, modifier)
        }
  }
}

@Preview
@Composable
private fun Preview2() {
  DynamicDicePrototypeTheme {
    ItemListScreen(
        items = listOf("group", "group2"),
        onSelect = {},
        menuActions = listOf(),
        onCreateItem = {},
        getKey = { it }) { item, isCompact, modifier ->
          DiceGroupItem(item = item, isCompact = isCompact, modifier = modifier)
        }
  }
}
