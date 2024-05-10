package com.example.dynamicdiceprototype.DTO.set

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

data class DiceSetDTO(
    val images: List<String>,
    var backgroundColor: Int = Color(0xFFCCCCCC).toArgb()
)
