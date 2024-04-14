package com.example.dynamicdiceprototype

import kotlin.random.Random

data class Dice(
    val id: Int = nextId(),
    val layers: List<Layer>,
    var current: Layer? = null,
    var state: DiceState = DiceState.UNLOCKED,
    var rotation: Float = 0F,
) {

  companion object {
    private var lastId = 0

    fun nextId(): Int {
      lastId += 1
      return lastId
    }
  }

  init {
    if (current === null) current = roll()
  }

  fun roll(): Layer {
    rotation = ((Random.nextFloat() * (15) + 5) * if (Random.nextBoolean()) -1 else 1)
    return layers.random()
  }
}

enum class DiceState {
  LOCKED,
  UNLOCKED
}
