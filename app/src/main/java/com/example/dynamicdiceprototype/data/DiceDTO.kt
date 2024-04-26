package com.example.dynamicdiceprototype.data

import androidx.compose.ui.graphics.Color

data class DiceDTO(
    val id: Int = Dice.nextId(),
    val name: String = "diceName",
    val layers: List<String>,
    var backgroundColor: Color = Color(0xFFCCCCCC)
)
