package com.example.dynamicdiceprototype.data

import kotlinx.serialization.Serializable

@Serializable
data class DiceGroup(
    var id: String = "",
    var name: String = "group",
    val dices: Map<String, Int> = mapOf(),
    val states: List<String> = listOf()
) {
  init {
    if (id.isEmpty()) id = generateUniqueID()
  }
}
