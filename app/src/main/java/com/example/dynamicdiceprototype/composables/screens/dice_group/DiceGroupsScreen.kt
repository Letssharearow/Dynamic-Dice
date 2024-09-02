package com.example.dynamicdiceprototype.composables.screens.dice_group

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.dynamicdiceprototype.composables.common.ItemListScreen
import com.example.dynamicdiceprototype.data.DiceGroup
import com.example.dynamicdiceprototype.data.MenuItem
import com.example.dynamicdiceprototype.services.PreferenceKey
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

@Composable
fun DiceGroupsScreen(
    groups: List<DiceGroup>,
    onSelectGroup: (groupName: DiceGroup) -> Unit,
    menuActions: List<MenuItem<DiceGroup>>,
    onCreateNewGroup: () -> Unit,
    onCreateRandomGroup: () -> Unit,
) {
  ItemListScreen(
      items = groups.sortedBy { it.name.uppercase() },
      onSelect = onSelectGroup,
      menuActions = menuActions,
      onCreateItem = onCreateNewGroup,
      getKey = { it.id },
      onRandomItem = onCreateRandomGroup,
      preferenceView = PreferenceKey.IsDicesGroupViewCompact) { item, isCompact, modifier ->
        DiceGroupItem(item = item.name, isCompact = isCompact, modifier = modifier)
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
            modifier =
                (if (isCompact) Modifier.height(IntrinsicSize.Max) else Modifier.height(70.dp))
                    .padding(8.dp)) {
              Text(
                  text = item,
                  style =
                      if (isCompact) MaterialTheme.typography.titleMedium
                      else MaterialTheme.typography.titleLarge,
                  modifier = Modifier.weight(1f))
              Icon(
                  painter = painterResource(id = R.drawable.pouch),
                  contentDescription = "Pouch",
                  modifier = Modifier.size(if (isCompact) 24.dp else 70.dp))
            }
      }
}

@Preview(showBackground = true)
@Composable
private fun DiceGroupsScreenPreview2() {
  DynamicDicePrototypeTheme { DiceGroupItem(item = "kniffel") }
}
