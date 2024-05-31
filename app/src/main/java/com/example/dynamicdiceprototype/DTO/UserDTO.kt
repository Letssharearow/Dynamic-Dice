package com.example.dynamicdiceprototype.DTO

import com.example.dynamicdiceprototype.data.DiceGroup

data class UserDTO(
    var dices: List<String> = listOf(),
    var diceGroups: Map<String, DiceGroup> = mapOf()
)
