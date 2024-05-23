package com.example.dynamicdiceprototype.composables

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dynamicdiceprototype.composables.common.AlertBox
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.services.TAG
import com.example.dynamicdiceprototype.services.getDices
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

@Composable
fun LandingPage(
    dices: List<Dice>,
    name: String,
    isLoading: Boolean,
    onRollClicked: () -> Unit,
    modifier: Modifier = Modifier
) {

  Log.d(TAG, "Recompose LandingPage $name => $dices")
  var openDialog by remember { mutableStateOf(true) }

  Column(
      verticalArrangement = Arrangement.SpaceBetween,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(visible = !isLoading, modifier = Modifier.weight(1f)) {
          DiceBundle(dices = dices)
        }
        AnimatedVisibility(visible = isLoading, Modifier.weight(1f)) {
          CircularProgressIndicator(modifier = Modifier.wrapContentSize(align = Alignment.Center))
        }
        DiceButtonM3(onRollClicked = onRollClicked, modifier = Modifier.padding(vertical = 16.dp))
      }
  if (dices.none { it.current != null }) {
    AlertBox(
        title = "Roll Dices",
        text = "",
        isOpen = openDialog,
        onDismiss = { openDialog = false },
        conConfirm = {
          openDialog = false
          onRollClicked()
        })
  }
}

@Preview(showBackground = true)
@Composable
private fun prev() {
  DynamicDicePrototypeTheme {
    LandingPage(dices = getDices(5), name = "Test", isLoading = false, onRollClicked = { /*TODO*/})
  }
}
