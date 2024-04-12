package com.example.dynamicdiceprototype

data class Dice(
    val layers: List<Layer>,
    var current: Layer? = null,
    var state: DiceState = DiceState.UNLOCKED
) {

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

data class Contact(var name: Layer, val alias: List<Layer>) {
  fun randomName(): Layer {
    val random = alias.random()
    name = random
    return random
  }
}
