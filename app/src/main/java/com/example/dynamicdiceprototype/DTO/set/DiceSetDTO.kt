package com.example.dynamicdiceprototype.DTO.set

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

data class DiceSetDTO(
    val images: Map<String, Int>,
    var backgroundColor: Int = Color(0xFFCCCCCC).toArgb()
    // TODO add userId to ensure only the user can modify his dice
)
