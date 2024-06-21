package com.example.dynamicdiceprototype.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.dynamicdiceprototype.DTO.DiceDTO
import com.example.dynamicdiceprototype.utils.randomItemByWeight
import java.util.UUID
import kotlin.math.sign
import kotlin.random.Random

data class Dice(
    var id: String = "",
    var name: String = "diceName",
    var faces: List<Face> = listOf(),
    var current: Face? = null,
    var diceLockState: DiceLockState = DiceLockState.UNLOCKED,
    var rotation: Float = ((Random.nextFloat() * (15) + 5) * if (Random.nextBoolean()) -1 else 1),
    var backgroundColor: Color = Color(0x80C0C0C0)
) {
  init {
    if (id.isEmpty())
        id =
            name
                .substring(0, (20).coerceAtMost(name.length))
                .plus(generateUniqueID().substring(name.length.coerceAtMost(20)))
  }

  fun roll(): Dice {
    return (this.copy(
        current = if (faces.isEmpty()) null else faces.randomItemByWeight(),
        rotation =
            ((Random.nextFloat() * (15) + 5) *
                when {
                  rotation > 0 -> -1
                  rotation == 0f -> Random.nextInt().sign
                  else -> 1
                })))
  }

  fun reset(): Dice =
      this.copy(current = null, rotation = 0f, diceLockState = DiceLockState.UNLOCKED)
}

fun generateUniqueID(): String {
  return UUID.randomUUID().toString()
}

enum class DiceLockState {
  LOCKED,
  UNLOCKED
}

fun Dice.toDiceDTO(): DiceDTO =
    DiceDTO(
        name = name,
        images = this.faces.associate { Pair(it.contentDescription, it.weight) },
        backgroundColor = this.backgroundColor.toArgb())
