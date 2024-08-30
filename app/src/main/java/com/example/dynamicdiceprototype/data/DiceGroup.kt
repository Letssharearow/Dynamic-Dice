package com.example.dynamicdiceprototype.data

import getWeightsInRange
import kotlin.random.Random
import kotlinx.serialization.Serializable
import weightedRandom

@Serializable
data class DiceGroup(
    var id: String = "",
    var name: String = "",
    val dices: Map<String, Int> = mapOf(),
    val states: List<String> = listOf()
) {
  companion object {
    fun random(dices: Set<String>): DiceGroup {
      val newDices = mutableMapOf<String, Int>()
      val range = weightedRandom(getWeightsInRange(1, 3, 500, curve = 0.75))
      for (i in 1..range) {
        newDices[dices.random()] = if (Random.nextFloat() > 0.66) range else 1
      }
      return DiceGroup(
          name =
              listOf(
                      "Brillant idea",
                      "All the dice",
                      "Quick play",
                      "New Invention",
                      "Random Dice Group",
                      "Random",
                      "Some Dice Group",
                      "Result of Spamclick")
                  .random(),
          dices = newDices)
    }
  }

  init {
    if (id.isEmpty()) id = generateUniqueID()
  }
}
