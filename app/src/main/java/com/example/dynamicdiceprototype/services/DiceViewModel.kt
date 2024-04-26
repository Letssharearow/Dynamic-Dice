package com.example.dynamicdiceprototype.services

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamicdiceprototype.R
import com.example.dynamicdiceprototype.data.Configuration
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.DiceState
import com.example.dynamicdiceprototype.data.ImageModel
import com.example.dynamicdiceprototype.data.Layer
import kotlinx.coroutines.launch

// extend ViewModel to survive configuration change (landscape mode)
object DiceViewModel : ViewModel() {
  val firebase = FirebaseDataStore()
  var configuration: Configuration = Configuration()
  var dicesState by mutableStateOf(getDices(7)) //
  var imageMap by
      mutableStateOf(
          mapOf<String, ImageModel>(
              "2130968597" to
                  ImageModel(
                      firebase.base64ToBitmap(
                          "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"),
                      contentDescription = "bad Girl"),
          ))

  init {
    collectFlow()
  }

  private fun diceMapToList(dices: List<Dice>, images: Map<String, ImageModel>): List<Dice> {
    return dices.map { dice ->
      dice.copy(layers = dice.layers.map { layer -> layer.copy(data = images[layer.imageId]) })
    }
  }

  private fun collectFlow() {
    viewModelScope.launch {
      firebase.imagesFlow.collect { images ->
        imageMap = images
        val bundle = configuration.bundles[configuration.lastBundle]
        val dices =
            bundle?.map { key ->
              configuration.dices[key] ?: Dice(layers = listOf())
            } // TODO better handling for null Dice
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
