package com.example.dynamicdiceprototype.data

import com.example.dynamicdiceprototype.DTO.ImageDTO

data class DiceGroup(val dices: Map<String, Int> = mapOf(), val states: List<ImageDTO> = listOf())
