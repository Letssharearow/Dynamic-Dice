package com.example.dynamicdiceprototype.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dynamicdiceprototype.composables.common.ArrangedColumn
import com.example.dynamicdiceprototype.composables.common.PupMenuWithAlert
import com.example.dynamicdiceprototype.composables.createdice.DiceCard
import com.example.dynamicdiceprototype.composables.screens.DiceGroupItem
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.MenuItem
import com.example.dynamicdiceprototype.services.PreferenceKey
import com.example.dynamicdiceprototype.services.PreferenceManager
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.DiceD6
import compose.icons.fontawesomeicons.solid.Plus
import compose.icons.fontawesomeicons.solid.Subscript

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> ItemListScreen(
    items: List<T>,
    onSelect: (item: T) -> Unit,
    menuActions: List<MenuItem<T>>,
    getKey: (item: T) -> String,
    onCreateItem: (() -> Unit)?,
    modifier: Modifier = Modifier,
    onRandomItem: (() -> Unit)? = null,
    onSecondaryAction: (() -> Unit)? = null,
    preferenceView: PreferenceKey = PreferenceKey.IsDicesViewCompact,
    view: @Composable (item: T, isCompact: Boolean, modifier: Modifier) -> Unit,
) {

  val isCompact =
      PreferenceManager.getPreferenceFlow<Boolean>(preferenceView)
          .collectAsState(initial = preferenceView.defaultValue as Boolean)
          .value

  Box {
    Column(modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)) {
      // Toggle switch for isCompact
      ItemListTogglSwitch(
          checked = isCompact,
          onChecked = { PreferenceManager.saveData(preferenceView, !isCompact) })

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
    }
    if (onRandomItem != null || onCreateItem != null) {
      Row(
          Modifier.fillMaxWidth()
              .background(Color.Transparent)
              .align(Alignment.BottomCenter)
              .padding(bottom = 8.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically) {
            onRandomItem?.let {
              Button(
                  onClick = it,
                  border =
                      BorderStroke(
                          width = 2.dp, color = MaterialTheme.colorScheme.secondaryContainer),
                  colors =
                      ButtonDefaults.buttonColors(
                          containerColor = MaterialTheme.colorScheme.secondary)) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.DiceD6,
                        contentDescription = "random",
                        modifier = Modifier.size(24.dp))
                  }
            }
            Spacer(modifier = modifier.width(24.dp))
            onCreateItem?.let {
              Button(
                  border =
                      BorderStroke(
                          width = 2.dp, color = MaterialTheme.colorScheme.primaryContainer),
                  onClick = it) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Plus,
                        contentDescription = "random",
                        modifier = Modifier.size(36.dp))
                  }
            }
            Spacer(modifier = modifier.width(24.dp))
            onSecondaryAction?.let {
              Button(
                  border =
                      BorderStroke(
                          width = 2.dp, color = MaterialTheme.colorScheme.secondaryContainer),
                  onClick = it,
                  colors =
                      ButtonDefaults.buttonColors(
                          containerColor = MaterialTheme.colorScheme.secondary)) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Subscript,
                        contentDescription = "random",
                        modifier = Modifier.size(24.dp))
                  }
            }
          }
    }
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
        getKey = { it.id },
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
        onRandomItem = {},
        onSecondaryAction = {},
        getKey = { it }) { item, isCompact, modifier ->
          DiceGroupItem(item = item, isCompact = isCompact, modifier = modifier)
        }
  }
}
