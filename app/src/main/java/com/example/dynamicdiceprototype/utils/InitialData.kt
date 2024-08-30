package com.example.dynamicdiceprototype.utils

import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.dynamicdiceprototype.DTO.DiceDTO
import com.example.dynamicdiceprototype.DTO.FaceDTO
import com.example.dynamicdiceprototype.DTO.ImageDTO
import com.example.dynamicdiceprototype.DTO.toDice
import com.example.dynamicdiceprototype.R
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.DiceGroup
import com.example.dynamicdiceprototype.services.FirebaseDataStore

private const val dice_standard = "Classic Die"
private const val dice_20 = "D20"
private const val dice_4 = "D4"
private const val dice_8 = "D8"
private const val dice_10 = "D10"
private const val dice_12 = "D12"
private const val dice_6_biased = "D6 Biased"
private const val dice_animals = "Die with Animals"
private const val dice_colors = "Die with Colors"

private const val image_frog = "Frog"
private const val image_lion = "Lion"
private const val image_monkey = "Monkey"

private const val image_one = "one"
private const val image_two = "two"
private const val image_three = "three"
private const val image_four = "four"
private const val image_five = "five"
private const val image_six = "six"

private const val image_red = "Red"
private const val image_orange = "Orange"
private const val image_yellow = "Yellow"
private const val image_green = "Green"
private const val image_blue = "Blue"
private const val image_purple = "Purple"

fun getInitialImages(res: Resources): List<ImageDTO> {
  data class ImageModelSetDTO(val image: Int, val name: String)

  val images =
      arrayOf<ImageModelSetDTO>(
          ImageModelSetDTO(image = R.drawable.frog, name = image_frog),
          ImageModelSetDTO(image = R.drawable.lion, name = image_lion),
          ImageModelSetDTO(image = R.drawable.monkey, name = image_monkey),
          ImageModelSetDTO(image = R.drawable.one_transparent, name = image_one),
          ImageModelSetDTO(image = R.drawable.two_transparent, name = image_two),
          ImageModelSetDTO(image = R.drawable.three_transparent, name = image_three),
          ImageModelSetDTO(image = R.drawable.four_transparent, name = image_four),
          ImageModelSetDTO(image = R.drawable.five_transparent, name = image_five),
          ImageModelSetDTO(image = R.drawable.six_transparent, name = image_six),
      )

  val imageDTOS =
      images
          .map {
            ImageDTO(
                contentDescription = it.name,
                base64String =
                    FirebaseDataStore.bitmapToBase64(BitmapFactory.decodeResource(res, it.image)))
          }
          .toMutableList()

  val imageCreator = ImageCreator()

  val colors =
      listOf(
          Pair(Color.Red, image_red),
          Pair(Color(0xFFFF7F00), image_orange),
          Pair(Color.Yellow, image_yellow),
          Pair(Color.Green, image_green),
          Pair(Color.Blue, image_blue),
          Pair(Color(0xFF800080), image_purple),
      )

  imageDTOS.addAll(
      colors.map { color ->
        val namedColor = color.second
        val bitmap = imageCreator.getBitmap(400, color.first.toArgb())
        ImageDTO(
            contentDescription = namedColor,
            base64String = FirebaseDataStore.bitmapToBase64(bitmap))
      })

  return imageDTOS
}

fun getInitialDices(): List<Dice> {

  val dices = mutableListOf<Pair<String, DiceDTO>>()
  val list = mutableListOf<FaceDTO>()

  for (i in 1..20) {
    list.add(FaceDTO(contentDescription = imageDTO_number_contentDescription, 1, i))
    if (i == 4) {
      dices.add(
          Pair(
              dice_4,
              DiceDTO(
                  name = dice_4, backgroundColor = Color.Green.toArgb(), images = list.toList())))
    }
    if (i == 8) {
      dices.add(
          Pair(
              dice_8,
              DiceDTO(
                  name = dice_8, backgroundColor = Color.Magenta.toArgb(), images = list.toList())))
    }
    if (i == 10) {
      dices.add(
          Pair(
              dice_10,
              DiceDTO(
                  name = dice_10,
                  backgroundColor = Color(0xFFFF0099).toArgb(),
                  images = list.toList())))
    }
    if (i == 12) {
      dices.add(
          Pair(
              dice_12,
              DiceDTO(
                  name = dice_12, backgroundColor = Color.Red.toArgb(), images = list.toList())))
    }
    if (i == 20) {
      dices.add(
          Pair(
              dice_20,
              DiceDTO(
                  name = dice_20,
                  backgroundColor = Color(0xFFFD7933).toArgb(),
                  images = list.toList())))
    }
  }

  dices.addAll(
      listOf(
          Pair(
              dice_animals,
              DiceDTO(
                  name = dice_animals,
                  images =
                      listOf(
                          (FaceDTO(image_monkey, 1, 1)),
                          (FaceDTO(image_lion, 1, 1)),
                          (FaceDTO(image_frog, 1, 1)),
                      ))),
          Pair(
              dice_colors,
              DiceDTO(
                  name = dice_colors,
                  images =
                      listOf(
                          (FaceDTO(image_red, 1, 1)),
                          (FaceDTO(image_orange, 1, 1)),
                          (FaceDTO(image_green, 1, 1)),
                          (FaceDTO(image_blue, 1, 1)),
                          (FaceDTO(image_purple, 1, 1)),
                          (FaceDTO(image_yellow, 1, 1)),
                      ))),
          Pair(
              dice_standard,
              DiceDTO(
                  name = dice_standard,
                  images =
                      listOf(
                          (FaceDTO(contentDescription = image_six, 1, 6)),
                          (FaceDTO(contentDescription = image_five, 1, 5)),
                          (FaceDTO(contentDescription = image_four, 1, 4)),
                          (FaceDTO(contentDescription = image_three, 1, 3)),
                          (FaceDTO(contentDescription = image_two, 1, 2)),
                          (FaceDTO(contentDescription = image_one, 1, 1)),
                      ))),
          Pair(
              dice_6_biased,
              DiceDTO(
                  name = dice_6_biased,
                  images =
                      listOf(
                          (FaceDTO(contentDescription = image_six, 5, 6)),
                          (FaceDTO(contentDescription = image_five, 1, 5)),
                          (FaceDTO(contentDescription = image_four, 1, 4)),
                          (FaceDTO(contentDescription = image_three, 1, 3)),
                          (FaceDTO(contentDescription = image_two, 1, 2)),
                          (FaceDTO(contentDescription = image_one, 1, 1)),
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
              dices = mapOf(dice_standard to 5),
          ),
          DiceGroup(
              name = "3 dice with states (click die)",
              id = "3 dice with states click die",
              states = listOf(image_frog, image_lion, image_monkey),
              dices = mapOf(dice_20 to 3),
          ),
          DiceGroup(
              id = temp_group_id,
              name = "Temp group, duplicate to save",
              dices = mapOf(dice_standard to 2, dice_20 to 2)),
      )
  return diceGroups
}
