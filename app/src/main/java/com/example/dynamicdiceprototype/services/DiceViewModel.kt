package com.example.dynamicdiceprototype.services

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamicdiceprototype.R
import com.example.dynamicdiceprototype.data.Configuration
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.DiceState
import com.example.dynamicdiceprototype.data.Layer
import kotlinx.coroutines.launch

// extend ViewModel to survive configuration change (landscape mode)
class DiceViewModel : ViewModel() {
  val firebase = FirebaseDataStore()
  var configuration: Configuration = Configuration()
  var dicesState by mutableStateOf(getDices(7)) //

  init {
    collectFlow()
  }

  private fun diceMapToList(map: Map<String, Dice>, images: Map<String, ImageBitmap>): List<Dice> {
    return map.values.map { dice ->
      dice.copy(layers = dice.layers.map { layer -> layer.copy(data = images[layer.imageId]) })
    }
  }

  private fun collectFlow() {
    viewModelScope.launch {
      firebase.imagesFlow.collect { images ->
        val dices = configuration.configuration[configuration.lastBundle]
        if (dices != null) dicesState = diceMapToList(dices, images)
      }
    }
  }

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
    val list = mutableListOf<Dice>()
    for (i in 1..n) {
      list.add(
          Dice(
              layers =
                  listOf(
                      Layer(imageId = "${R.drawable.one_transparent}"),
                      Layer(imageId = "${R.drawable.two_transparent}"),
                      Layer(imageId = "${R.drawable.three_transparent}"),
                      Layer(imageId = "${R.drawable.four_transparent}"),
                      Layer(imageId = "${R.drawable.five_transparent}"),
                      Layer(imageId = "${R.drawable.six_transparent}"))))
    }
    return list
  }
}
