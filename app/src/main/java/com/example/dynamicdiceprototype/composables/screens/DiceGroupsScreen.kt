package com.example.dynamicdiceprototype.composables.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dynamicdiceprototype.R
import com.example.dynamicdiceprototype.composables.ItemListScreen
import com.example.dynamicdiceprototype.data.MenuItem
import com.example.dynamicdiceprototype.services.PreferenceView
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

@Composable
fun DiceGroupsScreen(
    groups: List<String>,
    onSelectGroup: (groupId: String) -> Unit,
    menuActions: List<MenuItem<String>>,
    onCreateNewGroup: () -> Unit
) {
  ItemListScreen(
      items = groups,
      onSelect = onSelectGroup,
      menuActions = menuActions,
      onCreateItem = onCreateNewGroup,
      getKey = { it },
      preferenceView = PreferenceView.Group) { item, isCompact, modifier ->
        DiceGroupItem(item = item, isCompact = isCompact, modifier = modifier)
      }
}

@Composable
fun DiceGroupItem(
    item: String,
    modifier: Modifier = Modifier,
    isCompact: Boolean = true,
) {
  Surface(
      shadowElevation = 8.dp,
      color = MaterialTheme.colorScheme.secondary,
      modifier =
          modifier
              .border(
                  BorderStroke(2.dp, MaterialTheme.colorScheme.secondary),
                  RoundedCornerShape(16.dp))
              .clip(RoundedCornerShape(16.dp))) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier.height(if (isCompact) 35.dp else 70.dp).padding(8.dp)) {
              Text(
                  text = item,
                  style =
                      if (isCompact) MaterialTheme.typography.titleMedium
                      else MaterialTheme.typography.titleLarge,
                  modifier = Modifier.weight(1f))
              Icon(
                  painter = painterResource(id = R.drawable.pouch),
                  contentDescription = "Pouch",
              )
            }
      }
}

@Preview(showSystemUi = true)
@Composable
private fun DiceGroupsScreenPreview() {
  DynamicDicePrototypeTheme {
    DiceGroupsScreen(
        groups =
            listOf(
                "kniffel",
                "sonst.",
                "WürfelSpielX",
                "Long Name for a Dice Group, Wow this is soo long"),
        onSelectGroup = {},
        menuActions = listOf(),
        onCreateNewGroup = {},
    )
  }
}
