package com.example.dynamicdiceprototype.data

import androidx.compose.ui.graphics.Color

data class DiceDTO(
    val name: String = "diceName",
    val faces: List<String>,
    var backgroundColor: Color = Color(0xFFCCCCCC)
)
