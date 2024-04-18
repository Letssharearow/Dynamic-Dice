package com.example.dynamicdiceprototype

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// extend ViewModel to survive configuration change (landscape mode)
class DiceViewModel : ViewModel() {
  var dicesState by mutableStateOf(getDices(7)) //

  // Function to update a single dice
  fun lockDice(dice: Dice) {
    dicesState =
        // use Map function to trigger recomposition
        dicesState.map {
          if (it == dice) {
            // use copy function to trigger recomposition
            if (dice.state === DiceState.UNLOCKED)
                dice.copy(state = DiceState.LOCKED, rotation = 0F)
            else {
              dice.copy(state = DiceState.UNLOCKED, rotation = 0F)
            }
          } else it
        }
  }

  // Function to roll the dices
  fun rollDices() {
    dicesState =
        dicesState.map { dice ->
          if (dice.state != DiceState.LOCKED) {
            dice.copy(current = null) // set null to trigger recomposition and roll in Dice class
          } else {
            dice
          }
        }
  }

  fun getDices(n: Int = 5): List<Dice> {
    val list =
        mutableListOf(
            Dice(
                layers =
                    listOf(
                        Layer("1", imageId = R.drawable.one_transparent),
                        Layer("2", imageId = R.drawable.two_transparent),
                        Layer("3", imageId = R.drawable.three_transparent),
                        Layer("4", imageId = R.drawable.four_transparent),
                        Layer("5", imageId = R.drawable.five_transparent),
                        Layer("6", imageId = R.drawable.six_transparent))))
    for (i in 1..n) {
      list.add(
          Dice(
              layers =
                  listOf(
                      Layer("1", imageId = R.drawable.one_transparent),
                      Layer("2", imageId = R.drawable.two_transparent),
                      Layer("3", imageId = R.drawable.three_transparent),
                      Layer("4", imageId = R.drawable.four_transparent),
                      Layer("5", imageId = R.drawable.five_transparent),
                      Layer("6", imageId = R.drawable.six_transparent))))
    }
    return list
  }
}
