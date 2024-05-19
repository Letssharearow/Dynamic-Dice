package com.example.dynamicdiceprototype.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.dynamicdiceprototype.DTO.DiceDTO
import com.example.dynamicdiceprototype.utils.randomItemByWeight
import kotlin.random.Random

data class Dice(
    var name: String = "diceName",
    var faces: List<Face> = listOf(),
    var current: Face? = null,
    var state: DiceState = DiceState.UNLOCKED,
    var rotation: Float = ((Random.nextFloat() * (15) + 5) * if (Random.nextBoolean()) -1 else 1),
    var backgroundColor: Color = Color(0x80C0C0C0)
) {

  init {
    //    if ((current === null || !faces.contains(current)) && faces.isNotEmpty()) current = roll()
  }

  fun roll2() {
    current = if (faces.isEmpty()) null else faces.random()
  }

  fun roll(): Dice {

    return (this.copy(
        current = if (faces.isEmpty()) null else faces.randomItemByWeight(),
        rotation = ((Random.nextFloat() * (15) + 5) * if (rotation > 0) -1 else 1)))
  }
}

enum class DiceState {
  LOCKED,
  UNLOCKED
}

fun Dice.toDiceGetDTO(): DiceDTO =
    DiceDTO(
        images = this.faces.associate { Pair(it.contentDescription, it.weight) },
        backgroundColor = this.backgroundColor.toArgb())
