package com.example.dynamicdiceprototype.utils

import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.dynamicdiceprototype.DTO.DiceDTO
import com.example.dynamicdiceprototype.DTO.ImageDTO
import com.example.dynamicdiceprototype.DTO.UserDTO
import com.example.dynamicdiceprototype.ImageCreator
import com.example.dynamicdiceprototype.R
import com.example.dynamicdiceprototype.services.FirebaseDataStore
import com.example.dynamicdiceprototype.services.USER

fun uploadImages(res: Resources) {
  val firebase = FirebaseDataStore()
  data class ImageModelSetDTO(val image: Int, val name: String)
  val images =
      arrayOf<ImageModelSetDTO>(
          ImageModelSetDTO(image = R.drawable.cameleon, name = "cameleon"),
          ImageModelSetDTO(image = R.drawable.elephant, name = "elephant"),
          ImageModelSetDTO(image = R.drawable.fish, name = "fish"),
          ImageModelSetDTO(image = R.drawable.frog, name = "frog"),
          ImageModelSetDTO(image = R.drawable.lion, name = "lion"),
          ImageModelSetDTO(image = R.drawable.monkey, name = "monkey"),
          ImageModelSetDTO(image = R.drawable.owl, name = "owl"),
          ImageModelSetDTO(image = R.drawable.parrot, name = "parrot"),
          ImageModelSetDTO(image = R.drawable.penguin, name = "penguin"),
          ImageModelSetDTO(image = R.drawable.one_transparent, name = "one_transparent"),
          ImageModelSetDTO(image = R.drawable.two_transparent, name = "two_transparent"),
          ImageModelSetDTO(image = R.drawable.three_transparent, name = "three_transparent"),
          ImageModelSetDTO(image = R.drawable.four_transparent, name = "four_transparent"),
          ImageModelSetDTO(image = R.drawable.five_transparent, name = "five_transparent"),
          ImageModelSetDTO(image = R.drawable.six_transparent, name = "six_transparent"),
          ImageModelSetDTO(image = R.drawable.wizard_blue, name = "wizard blue"),
          ImageModelSetDTO(image = R.drawable.wizard_green, name = "wizard green"),
          ImageModelSetDTO(image = R.drawable.wizard_red, name = "wizard red"),
          ImageModelSetDTO(image = R.drawable.wizard_yellow, name = "wizard yellow"),
          ImageModelSetDTO(image = R.drawable.wizard_narr, name = "wizard narr"),
          ImageModelSetDTO(image = R.drawable.wizard_wizard, name = "wizard wizard"),
      )
  images.forEach {
    var bitmap = BitmapFactory.decodeResource(res, it.image)
    firebase.uploadImageDTO(
        ImageDTO(
            contentDescription = it.name, base64String = FirebaseDataStore.bitmapToBase64(bitmap)))
  }

  val imageCreator = ImageCreator()

  // Assuming you have a list of colors
  val colors =
      listOf(
          Pair(Color.Red, "red"),
          Pair(Color(0xFFFF7F00), "orange"), // Orange
          Pair(Color.Yellow, "yellow"),
          Pair(Color.Green, "green"),
          Pair(Color.Blue, "blue"),
          Pair(Color(0xFF4B0082), "indigo"), // Indigo
          Pair(Color(0xFF8B00FF), "violet"), // Violet
          // Additional colors
          Pair(Color(0xFFFFC0CB), "pink"), // Pink
          Pair(Color(0xFFFFD700), "gold"), // Gold
          Pair(Color.Cyan, "cyan"),
          Pair(Color(0xFFFFA500), "light orange"), // Light Orange
          Pair(Color(0xFF800080), "purple"), // Purple
          Pair(Color.Magenta, "magenta"),
          Pair(Color(0xFFC0C0C0), "silver"), // Silver
          Pair(Color.Gray, "gray"),
          Pair(Color.Black, "black"),
          Pair(Color.White, "white"))

  for (color in colors) {
    val namedColor = color.second
    val bitmap = imageCreator.getBitmap(400, color.first.toArgb())
    firebase.uploadImageDTO(
        ImageDTO(
            contentDescription = namedColor,
            base64String = FirebaseDataStore.bitmapToBase64(bitmap)))
  }
}

fun uploadDices() {
  val firbase = FirebaseDataStore()
  val images =
      arrayOf(
          Pair(
              "red_and_green",
              DiceDTO(
                  mapOf(
                      Color.Red.toArgb().toString() to 1,
                      Color.Green.toArgb().toString() to 1,
                  ))),
          Pair(
              "animals",
              DiceDTO(
                  mapOf(
                      (R.drawable.cameleon.toString() to 1),
                      (R.drawable.elephant.toString() to 1),
                      (R.drawable.frog.toString() to 1),
                      (R.drawable.fish.toString() to 1),
                      (R.drawable.lion.toString() to 1),
                      (R.drawable.monkey.toString() to 1),
                      (R.drawable.owl.toString() to 1),
                      (R.drawable.parrot.toString() to 1),
                      (R.drawable.penguin.toString() to 1),
                  ))),
          Pair(
              "6er",
              DiceDTO(
                  mapOf(
                      (R.drawable.six_transparent.toString() to 1),
                      (R.drawable.five_transparent.toString() to 1),
                      (R.drawable.four_transparent.toString() to 1),
                      (R.drawable.three_transparent.toString() to 1),
                      (R.drawable.two_transparent.toString() to 1),
                      (R.drawable.one_transparent.toString() to 1),
                  ))))
  images.forEach { firbase.uploadDice(it.first, it.second) }
}

fun uploadUser() {
  val firbase = FirebaseDataStore()

  val diceGroups =
      mapOf(
          "Kniffel" to mapOf("6er" to 5),
          "animals count" to mapOf("6er" to 1, "animals" to 5),
      )
  val dices = listOf("random", "6er", "animals")

  firbase.uploadUserConfig(USER, UserDTO(dices = dices, diceGroups = diceGroups))
}
