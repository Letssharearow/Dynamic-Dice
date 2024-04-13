package com.example.dynamicdiceprototype

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// extend ViewModel to survive configuration change (landscape mode)
class DiceViewModel : ViewModel() {
  // LiveData to hold the list of dices
  private val _dices = MutableLiveData<List<Dice>>(getDices())
  val dices: LiveData<List<Dice>> = _dices
  var dicesState by mutableStateOf(getDices()) //

  // Function to update a single dice
  fun updateDice(dice: Dice) {
    _dices.value = _dices.value?.map { if (it.id == dice.id) dice else it }
  }

  // Function to roll the dices
  fun rollDices() {
    _dices.value =
        _dices.value?.map { dice ->
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
    )
  }
}
