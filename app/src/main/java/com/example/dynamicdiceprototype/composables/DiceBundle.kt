package com.example.dynamicdiceprototype.composables

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.services.TAG

@Composable
fun DiceBundle(dices: List<Dice>, modifier: Modifier = Modifier) {
  // Create a state variable to hold the current value of the dice
  Log.d(TAG, "Recompose DiceBundle => $dices")
  Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
    DicesView(dices)
  }
}
