package com.example.dynamicdiceprototype

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// extend ViewModel to survive configuration change (landscape mode)
class DiceViewModel : ViewModel() {
  var dicesState by mutableStateOf(getDices()) //

  // Function to update a single dice
  fun lockDice(dice: Dice) {
    Log.i("MyApp", "dicesState updateDice before $dicesState")
    dicesState =
        dicesState.map {
          if (it == dice) {
            dice.copy(state = DiceState.LOCKED)
          } else it
        }
    Log.i("MyApp", "dicesState updateDice after $dicesState")
  }

  // Function to roll the dices
  fun rollDices() {
    dicesState =
        dicesState.map { dice ->
          if (dice.state != DiceState.LOCKED) {
            dice.copy(current = dice.roll())
          } else {
            dice
          }
        }
  }

  fun getDices(): List<Dice> {
    return listOf(
        Dice(listOf(Layer("1"), Layer("2"), Layer("3"), Layer("4"), Layer("5"), Layer("6"))),
        Dice(listOf(Layer("1"), Layer("2"), Layer("3"), Layer("4"), Layer("5"), Layer("6"))),
        Dice(listOf(Layer("1"), Layer("2"), Layer("3"), Layer("4"), Layer("5"), Layer("6"))),
        Dice(listOf(Layer("1"), Layer("2"), Layer("3"), Layer("4"), Layer("5"), Layer("6"))),
        Dice(listOf(Layer("1"), Layer("2"), Layer("3"), Layer("4"), Layer("5"), Layer("6"))),
        Dice(listOf(Layer("1"), Layer("2"), Layer("3"), Layer("4"), Layer("5"), Layer("6"))),
    )
  }
}
