package com.example.dynamicdiceprototype.DTO.get

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

data class DiceGetDTO(
    val images: List<ImageGetDTO>,
    var backgroundColor: Int = Color(0xFFCCCCCC).toArgb()
)
