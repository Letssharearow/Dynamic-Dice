package com.example.dynamicdiceprototype

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// extend ViewModel to survive configuration change (landscape mode)
class DiceViewModel : ViewModel() {
  var dicesState by mutableStateOf(getDices()) //

  // Function to update a single dice
  fun lockDice(dice: Dice) {
    dicesState =
        // use Map function to trigger recomposition
        dicesState.map {
          if (it == dice) {
            // use copy function to trigger recomposition
            dice.copy(state = DiceState.LOCKED, rotation = 0F)
          } else it
        }
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
        Dice(
            layers =
                listOf(Layer("1"), Layer("2"), Layer("3"), Layer("4"), Layer("5"), Layer("6"))),
        Dice(
            layers =
                listOf(Layer("1"), Layer("2"), Layer("3"), Layer("4"), Layer("5"), Layer("6"))),
        Dice(
            layers =
                listOf(Layer("1"), Layer("2"), Layer("3"), Layer("4"), Layer("5"), Layer("6"))),
        Dice(
            layers =
                listOf(Layer("1"), Layer("2"), Layer("3"), Layer("4"), Layer("5"), Layer("6"))),
        Dice(
            layers =
                listOf(Layer("1"), Layer("2"), Layer("3"), Layer("4"), Layer("5"), Layer("6"))),
        Dice(
            layers =
                listOf(Layer("1"), Layer("2"), Layer("3"), Layer("4"), Layer("5"), Layer("6"))),
    )
  }
}
