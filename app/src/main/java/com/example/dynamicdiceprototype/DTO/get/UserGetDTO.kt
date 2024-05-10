package com.example.dynamicdiceprototype.DTO.get

data class UserGetDTO(
    var dices: List<Map<String, DiceGetDTO>>,
    var diceGroups: Map<String, Map<String, Int>>
)
