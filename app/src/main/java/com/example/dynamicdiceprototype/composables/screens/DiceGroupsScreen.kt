package com.example.dynamicdiceprototype.composables.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dynamicdiceprototype.R
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceGroupsScreen(
    groups: List<String>,
    onSelectGroup: (groupId: String) -> Unit,
    onCreateNewGroup: (number: Int) -> Unit
) {
  LazyVerticalGrid(columns = GridCells.Fixed(2)) {
    items(groups) { item ->
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(16.dp).clickable { onSelectGroup(item) }) {
            Icon(painter = painterResource(id = R.drawable.pouch), contentDescription = "Pouch")
            Text(text = item, style = MaterialTheme.typography.titleMedium, modifier = Modifier)
          }
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
        onCreateNewGroup = {})
  }
}
