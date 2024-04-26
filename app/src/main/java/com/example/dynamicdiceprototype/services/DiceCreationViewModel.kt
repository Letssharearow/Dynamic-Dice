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

  fun updateSelectedLayers(layers: Map<String, Layer>) {
    dice = dice.copy(layers = layers.values.toList())
  }

  fun updateBackgroundColor(color: Color) {
    dice = dice.copy(backgroundColor = color)
  }

  fun saveDice() {
    templates = templates + dice
  }

  fun changeWeight(weight: Int, imageId: String) {
    dice =
        dice.copy(
            layers =
                dice.layers.map { if (it.imageId == imageId) it.copy(weight = weight) else it })
  }
}
