package com.example.dynamicdiceprototype.DTO

data class UserDTO(
    var dices: List<String> = listOf(),
    var diceGroups: Map<String, Map<String, Int>> = mapOf()
)
