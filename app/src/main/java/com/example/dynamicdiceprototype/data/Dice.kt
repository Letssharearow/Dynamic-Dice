package com.example.dynamicdiceprototype.data

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class Dice(
    var name: String = "diceName",
    var faces: List<Face>,
    var current: Face? = null,
    var state: DiceState = DiceState.UNLOCKED,
    var rotation: Float = ((Random.nextFloat() * (15) + 5) * if (Random.nextBoolean()) -1 else 1),
    var backgroundColor: Color = Color(0xFFCCCCCC)
) {

  init {
    if ((current === null || !faces.contains(current)) && faces.isNotEmpty()) current = roll()
  }

  fun roll(): Face {
    // always flip to the other side to show that the face is a new face
    rotation = ((Random.nextFloat() * (15) + 5) * if (rotation > 0) -1 else 1)
    return faces.random()
  }
}

enum class DiceState {
  LOCKED,
  UNLOCKED
}
