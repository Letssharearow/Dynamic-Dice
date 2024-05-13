package com.example.dynamicdiceprototype.DTO.get

data class UserDTO(
    var dices: List<String> = listOf(),
    var diceGroups: Map<String, Map<String, Int>> = mapOf()
)
