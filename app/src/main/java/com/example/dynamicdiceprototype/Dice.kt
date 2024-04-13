package com.example.dynamicdiceprototype

data class Dice(
    val layers: List<Layer>,
    var current: Layer? = null,
    var state: DiceState = DiceState.UNLOCKED,
    val id: Int = nextId()
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
    return layers.random()
  }
}

enum class DiceState {
  LOCKED,
  UNLOCKED
}
