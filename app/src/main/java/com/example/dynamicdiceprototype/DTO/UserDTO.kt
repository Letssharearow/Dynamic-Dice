package com.example.dynamicdiceprototype.DTO

import com.example.dynamicdiceprototype.data.DiceGroup
import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    var dices: List<String> = listOf(),
    var diceGroups: Map<String, DiceGroup> = mapOf()
)
