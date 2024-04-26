package com.example.dynamicdiceprototype.services

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.Face

class DiceCreationViewModel(dices: Map<String, Dice>) : ViewModel() {
  var dice by mutableStateOf<Dice>(Dice(faces = listOf()))
  var templates by mutableStateOf<List<Dice>>(dices.values.toList())
  var facesSize by mutableStateOf<Int>(20)

  fun createNewDice(name: String, numFaces: Int) {
    facesSize = numFaces
    dice = Dice(name = name, faces = listOf())
  }

  fun updateSelectedFaces(faces: Map<String, Face>) {
    dice = dice.copy(faces = faces.values.toList())
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
            faces = dice.faces.map { if (it.imageId == imageId) it.copy(weight = weight) else it })
  }
}
