package com.example.dynamicdiceprototype.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dynamicdiceprototype.composables.common.ArrangedColumn
import com.example.dynamicdiceprototype.composables.common.ContinueButton
import com.example.dynamicdiceprototype.composables.common.PupMenuWithAlert
import com.example.dynamicdiceprototype.composables.createdice.DiceCard
import com.example.dynamicdiceprototype.composables.screens.DiceGroupItem
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.MenuItem
import com.example.dynamicdiceprototype.services.PreferenceView
import com.example.dynamicdiceprototype.services.PreferencesService
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun <T> ItemListScreen(
    items: List<T>,
    onSelect: (item: T) -> Unit,
    menuActions: List<MenuItem<T>>,
    getKey: (item: T) -> String,
    onCreateItem: () -> Unit,
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
    ItemListTogglSwitch(
        checked = isCompact,
        onChecked = {
          isCompact = it
          preferencesService.saveIsCompact(context, isCompact, preferenceView)
        })

    ArrangedColumn(Modifier.weight(1f)) {
      LazyColumn {
        items(items = items, key = getKey) { item ->
          var showMenu by remember { mutableStateOf(false) }
          Box(
              modifier =
                  Modifier.combinedClickable(
                          onClick = { onSelect(item) }, onLongClick = { showMenu = true })
                      .fillMaxWidth()
                      .padding(bottom = 8.dp)) {
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
    CreateNewItemButton { onCreateItem() }
  }
}

@Composable
fun CreateNewItemButton(onClick: () -> Unit) {
  Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
    ContinueButton(
        onClick = onClick, // TODO Better handling?
        text = "Create New +",
    )
  }
}

@Composable
fun ItemListTogglSwitch(checked: Boolean, onChecked: (Boolean) -> Unit) {
  Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.End) {
        Text("Toggle View", Modifier.padding(end = 8.dp))
        Switch(checked = checked, onCheckedChange = onChecked)
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
