package com.example.dynamicdiceprototype.utils

import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.dynamicdiceprototype.R
import com.example.dynamicdiceprototype.data.DTO.DiceDTO
import com.example.dynamicdiceprototype.data.DTO.FaceDTO
import com.example.dynamicdiceprototype.data.DTO.ImageDTO
import com.example.dynamicdiceprototype.data.DTO.toDice
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.DiceGroup

private const val DICE_STANDARD = "Classic Die"
private const val DICE_20 = "D20"
private const val DICE_4 = "D4"
private const val DICE_8 = "D8"
private const val DICE_10 = "D10"
private const val DICE_12 = "D12"
private const val DICE_6_BIASED = "D6 Biased"
private const val DICE_ANIMALS = "Die with Animals"
private const val DICE_COLORS = "Die with Colors"

private const val IMAGE_FROG = "Frog"
private const val IMAGE_LION = "Lion"
private const val IMAGE_MONKEY = "Monkey"

private const val IMAGE_ONE = "one"
private const val IMAGE_TWO = "two"
private const val IMAGE_THREE = "three"
private const val IMAGE_FOUR = "four"
private const val IMAGE_FIVE = "five"
private const val IMAGE_SIX = "six"

private const val IMAGE_RED = "Red"
private const val IMAGE_ORANGE = "Orange"
private const val IMAGE_YELLOW = "Yellow"
private const val IMAGE_GREEN = "Green"
private const val IMAGE_BLUE = "Blue"
private const val IMAGE_PURPLE = "Purple"

fun getInitialImages(res: Resources): List<ImageDTO> {
  data class ImageModelSetDTO(val image: Int, val name: String)

  val images =
      arrayOf<ImageModelSetDTO>(
          ImageModelSetDTO(image = R.drawable.frog, name = IMAGE_FROG),
          ImageModelSetDTO(image = R.drawable.lion, name = IMAGE_LION),
          ImageModelSetDTO(image = R.drawable.monkey, name = IMAGE_MONKEY),
          ImageModelSetDTO(image = R.drawable.one_transparent, name = IMAGE_ONE),
          ImageModelSetDTO(image = R.drawable.two_transparent, name = IMAGE_TWO),
          ImageModelSetDTO(image = R.drawable.three_transparent, name = IMAGE_THREE),
          ImageModelSetDTO(image = R.drawable.four_transparent, name = IMAGE_FOUR),
          ImageModelSetDTO(image = R.drawable.five_transparent, name = IMAGE_FIVE),
          ImageModelSetDTO(image = R.drawable.six_transparent, name = IMAGE_SIX),
      )

  val imageDTOS =
      images
          .map {
            ImageDTO(
                contentDescription = it.name,
                base64String =
                    ImageMapper.bitmapToBase64(BitmapFactory.decodeResource(res, it.image)))
          }
          .toMutableList()

  val imageMapper = ImageMapper()

  val colors =
      listOf(
          Pair(Color.Red, IMAGE_RED),
          Pair(Color(0xFFFF7F00), IMAGE_ORANGE),
          Pair(Color.Yellow, IMAGE_YELLOW),
          Pair(Color.Green, IMAGE_GREEN),
          Pair(Color.Blue, IMAGE_BLUE),
          Pair(Color(0xFF800080), IMAGE_PURPLE),
      )

  imageDTOS.addAll(
      colors.map { color ->
        val namedColor = color.second
        val bitmap = imageMapper.getBitmap(400, color.first.toArgb())
        ImageDTO(contentDescription = namedColor, base64String = ImageMapper.bitmapToBase64(bitmap))
      })

  return imageDTOS
}

fun getInitialDices(): List<Dice> {

  val dices = mutableListOf<Pair<String, DiceDTO>>()
  val list = mutableListOf<FaceDTO>()

  for (i in 1..20) {
    list.add(FaceDTO(contentDescription = IMAGE_DTO_NUMBER_CONTENT_DESCRIPTION, 1, i))
    if (i == 4) {
      dices.add(
          Pair(
              DICE_4,
              DiceDTO(
                  name = DICE_4, backgroundColor = Color.Green.toArgb(), images = list.toList())))
    }
    if (i == 8) {
      dices.add(
          Pair(
              DICE_8,
              DiceDTO(
                  name = DICE_8, backgroundColor = Color.Magenta.toArgb(), images = list.toList())))
    }
    if (i == 10) {
      dices.add(
          Pair(
              DICE_10,
              DiceDTO(
                  name = DICE_10,
                  backgroundColor = Color(0xFFFF0099).toArgb(),
                  images = list.toList())))
    }
    if (i == 12) {
      dices.add(
          Pair(
              DICE_12,
              DiceDTO(
                  name = DICE_12, backgroundColor = Color.Red.toArgb(), images = list.toList())))
    }
    if (i == 20) {
      dices.add(
          Pair(
              DICE_20,
              DiceDTO(
                  name = DICE_20,
                  backgroundColor = Color(0xFFFD7933).toArgb(),
                  images = list.toList())))
    }
  }

  dices.addAll(
      listOf(
          Pair(
              DICE_ANIMALS,
              DiceDTO(
                  name = DICE_ANIMALS,
                  images =
                      listOf(
                          (FaceDTO(IMAGE_MONKEY, 1, 1)),
                          (FaceDTO(IMAGE_LION, 1, 1)),
                          (FaceDTO(IMAGE_FROG, 1, 1)),
                      ))),
          Pair(
              DICE_COLORS,
              DiceDTO(
                  name = DICE_COLORS,
                  images =
                      listOf(
                          (FaceDTO(IMAGE_RED, 1, 1)),
                          (FaceDTO(IMAGE_ORANGE, 1, 1)),
                          (FaceDTO(IMAGE_GREEN, 1, 1)),
                          (FaceDTO(IMAGE_BLUE, 1, 1)),
                          (FaceDTO(IMAGE_PURPLE, 1, 1)),
                          (FaceDTO(IMAGE_YELLOW, 1, 1)),
                      ))),
          Pair(
              DICE_STANDARD,
              DiceDTO(
                  name = DICE_STANDARD,
                  images =
                      listOf(
                          (FaceDTO(contentDescription = IMAGE_SIX, 1, 6)),
                          (FaceDTO(contentDescription = IMAGE_FIVE, 1, 5)),
                          (FaceDTO(contentDescription = IMAGE_FOUR, 1, 4)),
                          (FaceDTO(contentDescription = IMAGE_THREE, 1, 3)),
                          (FaceDTO(contentDescription = IMAGE_TWO, 1, 2)),
                          (FaceDTO(contentDescription = IMAGE_ONE, 1, 1)),
                      ))),
          Pair(
              DICE_6_BIASED,
              DiceDTO(
                  name = DICE_6_BIASED,
                  images =
                      listOf(
                          (FaceDTO(contentDescription = IMAGE_SIX, 5, 6)),
                          (FaceDTO(contentDescription = IMAGE_FIVE, 1, 5)),
                          (FaceDTO(contentDescription = IMAGE_FOUR, 1, 4)),
                          (FaceDTO(contentDescription = IMAGE_THREE, 1, 3)),
                          (FaceDTO(contentDescription = IMAGE_TWO, 1, 2)),
                          (FaceDTO(contentDescription = IMAGE_ONE, 1, 1)),
                      )))))

  return dices.map { it.second.toDice(it.first) }
}

fun getInitialDiceGroups(): List<DiceGroup> {
  val diceGroups =
      listOf(
          DiceGroup(
              name = "yahtzee",
              id = "yahtzee",
              states = listOf(),
              dices = mapOf(DICE_STANDARD to 5),
          ),
          DiceGroup(
              name = "3 dice with states (click die)",
              id = "3 dice with states click die",
              states = listOf(IMAGE_FROG, IMAGE_LION, IMAGE_MONKEY),
              dices = mapOf(DICE_20 to 3),
          ),
          DiceGroup(
              id = TEMP_GROUP_ID,
              name = "Temp group, duplicate to save",
              dices = mapOf(DICE_STANDARD to 2, DICE_20 to 2)),
      )
  return diceGroups
}
