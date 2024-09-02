package com.example.dynamicdiceprototype.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.dynamicdiceprototype.data.DTO.DiceDTO
import com.example.dynamicdiceprototype.data.DTO.FaceDTO
import com.example.dynamicdiceprototype.data.DTO.ImageDTO
import com.example.dynamicdiceprototype.data.DTO.toDice
import com.example.dynamicdiceprototype.utils.IMAGE_DTO_NUMBER_CONTENT_DESCRIPTION
import com.example.dynamicdiceprototype.utils.randomItemByWeight
import getWeightsInRange
import java.util.UUID
import kotlin.math.sign
import kotlin.random.Random
import weightedRandom

data class Dice(
    var id: String = "",
    var name: String = "diceName",
    var faces: List<Face> = listOf(),
    var current: Face? = null,
    var diceLockState: DiceLockState = DiceLockState.UNLOCKED,
    var rotation: Float = ((Random.nextFloat() * (15) + 5) * if (Random.nextBoolean()) -1 else 1),
    var state: Face? = null,
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
      this.copy(current = null, rotation = 0f, diceLockState = DiceLockState.UNLOCKED, state = null)

  companion object {

    fun random(images: Map<String, ImageDTO>): Dice {
      val faces = mutableListOf<FaceDTO>()

      val range = weightedRandom(getWeightsInRange(2, 6, 50, curve = 0.75))
      for (i in 1..range) {
        val contentDescription = images.values.random().contentDescription
        faces.add(
            FaceDTO(
                contentDescription = contentDescription,
                weight =
                    if (Random.nextInt(10) == 0) weightedRandom(getWeightsInRange(1, 1, 50, 0.75))
                    else 1,
                value = weightedRandom(getWeightsInRange(0, 6, 50, 0.999))))
      }

      val name =
          listOf(
                  "Brillant idea",
                  "Grandpa's heritage",
                  "The Truth Speaker",
                  "Unfair Dice",
                  "Random Die",
                  "Random",
                  "Some Die",
                  "Result of Spamclick")
              .random()
      val backgroundColor = Random.nextInt()
      return DiceDTO(name = name, backgroundColor = backgroundColor, images = faces)
          .toDice("", images)
    }

    fun numbered(start: Int, end: Int): Dice {
      return Dice(
          name = "numbered",
          faces =
              (start..end).map {
                Face(contentDescription = IMAGE_DTO_NUMBER_CONTENT_DESCRIPTION, value = it)
              })
    }
  }
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
        images =
            this.faces.map {
              FaceDTO(
                  contentDescription = it.contentDescription, weight = it.weight, value = it.value)
            },
        backgroundColor = this.backgroundColor.toArgb())
