package com.example.dynamicdiceprototype.DTO.get

data class UserGetDTO(var dices: List<String>, var diceGroups: Map<String, Map<String, Int>>)
