package com.example.dynamicdiceprototype.data

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class Dice(
    val id: Int = nextId(),
    val name: String = "diceName",
    val layers: Array<Layer>,
    var current: Layer? = null,
    var state: DiceState = DiceState.UNLOCKED,
    var rotation: Float = ((Random.nextFloat() * (15) + 5) * if (Random.nextBoolean()) -1 else 1),
    var backgroundColor: Color = Color(0xFFCCCCCC)
) {

  companion object {
    private var lastId = 0

    fun nextId(): Int {
      lastId += 1
      return lastId
    }
  }

  init {
    if ((current === null || !layers.contains(current)) && layers.isNotEmpty()) current = roll()
  }

  fun roll(): Layer {
    // always flip to the other side to show that the layer is a new layer
    rotation = ((Random.nextFloat() * (15) + 5) * if (rotation > 0) -1 else 1)
    return layers.random()
  }
}

enum class DiceState {
  LOCKED,
  UNLOCKED
}
