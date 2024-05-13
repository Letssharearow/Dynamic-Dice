package com.example.dynamicdiceprototype.DTO.get

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.Face

data class DiceDTO(
    val images: Map<String, Int> = mapOf(),
    var backgroundColor: Int = Color(0xFFCCCCCC).toArgb()
)

fun DiceDTO.toDice(name: String): Dice {
  val faces =
      this.images.map { image -> Face(contentDescription = image.key, weight = image.value) }

  return Dice(
      name = name, // Replace with actual logic to determine the name
      faces = faces,
      backgroundColor = Color(this.backgroundColor))
}
