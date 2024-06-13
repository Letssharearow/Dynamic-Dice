package com.example.dynamicdiceprototype.data

import kotlinx.serialization.Serializable

@Serializable
data class DiceGroup(val dices: Map<String, Int> = mapOf(), val states: List<String> = listOf())
