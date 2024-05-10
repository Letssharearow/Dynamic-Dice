package com.example.dynamicdiceprototype.utils

import android.content.res.Resources
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.dynamicdiceprototype.DTO.set.DiceSetDTO
import com.example.dynamicdiceprototype.DTO.set.UserSetDTO
import com.example.dynamicdiceprototype.services.FirebaseDataStore

fun uploadImages(res: Resources) {
  val firbase = FirebaseDataStore()
  data class ImageModelSetDTO(val image: Int, val name: String)
  val images =
      arrayOf<ImageModelSetDTO>(
          //          ImageModelSetDTO(image = R.drawable.cameleon, name = "cameleon"),
          //          ImageModelSetDTO(image = R.drawable.elephant, name = "elephant"),
          //          ImageModelSetDTO(image = R.drawable.fish, name = "fish"),
          //          ImageModelSetDTO(image = R.drawable.frog, name = "frog"),
          //          ImageModelSetDTO(image = R.drawable.lion, name = "lion"),
          //          ImageModelSetDTO(image = R.drawable.monkey, name = "monkey"),
          //          ImageModelSetDTO(image = R.drawable.owl, name = "owl"),
          //          ImageModelSetDTO(image = R.drawable.parrot, name = "parrot"),
          //          ImageModelSetDTO(image = R.drawable.penguin, name = "penguin"),
          //          ImageModelSetDTO(image = R.drawable.one_transparent, name =
          // "one_transparent"),
          //          ImageModelSetDTO(image = R.drawable.two_transparent, name =
          // "two_transparent"),
          //          ImageModelSetDTO(image = R.drawable.three_transparent, name =
          // "three_transparent"),
          //          ImageModelSetDTO(image = R.drawable.four_transparent, name =
          // "four_transparent"),
          //          ImageModelSetDTO(image = R.drawable.five_transparent, name =
          // "five_transparent"),
          //          ImageModelSetDTO(image = R.drawable.six_transparent, name =
          // "six_transparent"),
          )
  images.forEach {
    //    var bitmap = BitmapFactory.decodeResource(res, it.image)
    //    firbase.uploadBitmap("${it.image}", name = it.name, bitmap = bitmap)
  }
}

fun uploadDices() {
  val firbase = FirebaseDataStore()
  val images =
      arrayOf(
          Pair(
              "red_and_green",
              DiceSetDTO(
                  listOf(
                      Color.Red.toArgb().toString(),
                      Color.Green.toArgb().toString(),
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

  firbase.uploadUserConfig("juli", UserSetDTO(dices = dices, diceGroups = diceGroups))
}
