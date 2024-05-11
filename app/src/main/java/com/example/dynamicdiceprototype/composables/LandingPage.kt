package com.example.dynamicdiceprototype.composables

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.services.DiceViewModel
import com.example.dynamicdiceprototype.services.HeaderViewModel
import com.example.dynamicdiceprototype.services.TAG

@Composable
fun LandingPage(dices: List<Dice>, name: String, modifier: Modifier = Modifier) {
  val viewModel: DiceViewModel = viewModel<DiceViewModel>()
  val headerViewModel = viewModel<HeaderViewModel>()
  headerViewModel.changeHeaderText(name)

  Log.d(TAG, "Recompose LandingPage $name => $dices")
  Column(modifier) {
    DiceBundle(dices = dices, modifier = Modifier.weight(1f))
    DiceButtonM3(
        onRollClicked = { viewModel.rollDices() },
        modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 16.dp))
  }
}
