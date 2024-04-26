package com.example.dynamicdiceprototype.services

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.Layer

class DiceCreationViewModel(dices: Map<String, Dice>) : ViewModel() {
  var dice by mutableStateOf<Dice>(Dice(layers = listOf()))
  var templates by mutableStateOf<List<Dice>>(dices.values.toList())
  var layersSize by mutableStateOf<Int>(20)

  fun createNewDice(name: String, numLayers: Int) {
    layersSize = numLayers
    dice = Dice(name = name, layers = listOf())
  }

  fun updateSelectedImages(images: List<Layer>) {
    dice = dice.copy(layers = images)
  }

  fun updateBackgroundColor(color: Color) {
    dice = dice.copy(backgroundColor = color)
  }

  fun saveDice() {
    // Save the dice to the templates list
    templates = templates + (dice ?: return)
  }

  fun changeWeight(weight: Int, imageId: String) {
    dice =
        dice.copy(
            layers =
                dice.layers.map { if (it.imageId == imageId) it.copy(weight = weight) else it })
  }
}
