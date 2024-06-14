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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.Face
import com.example.dynamicdiceprototype.services.DiceViewModel
import com.example.dynamicdiceprototype.services.TAG
import com.example.dynamicdiceprototype.services.getDices
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

@Composable
fun LandingPage(
    dices: List<Dice>,
    name: String,
    states: List<Face>,
    isLoading: Boolean,
    onRollClicked: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DiceViewModel? = null
) {

  Log.d(TAG, "Recompose LandingPage $name => $dices")

  Column(
      verticalArrangement = Arrangement.SpaceBetween,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(visible = !isLoading, modifier = Modifier.weight(1f)) {
          DiceBundle(dices = dices, states = states, viewModel = viewModel)
        }
        AnimatedVisibility(visible = isLoading, Modifier.weight(1f)) {
          CircularProgressIndicator(modifier = Modifier.wrapContentSize(align = Alignment.Center))
        }
        DiceButtonM3(onRollClicked = onRollClicked, modifier = Modifier.padding(vertical = 16.dp))
      }
}

@Preview(showBackground = true)
@Composable
private fun prev() {
  DynamicDicePrototypeTheme {
    LandingPage(
        dices = getDices(5),
        name = "Test",
        isLoading = false,
        states = listOf(),
        onRollClicked = { /*TODO*/ })
  }
}
