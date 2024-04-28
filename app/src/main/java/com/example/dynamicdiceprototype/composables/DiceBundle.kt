package com.example.dynamicdiceprototype.composables

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.dynamicdiceprototype.data.Configuration.Dice
import com.example.dynamicdiceprototype.services.TAG

@Composable
fun DiceBundle(dices: List<Dice>, name: String, modifier: Modifier = Modifier) {
  // Create a state variable to hold the current value of the dice
  Log.d(TAG, "Recompose DiceBundle $name => $dices")
  Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
    Text(text = name, fontSize = 36.sp)
    DicesView(dices)
  }
}
