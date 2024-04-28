package com.example.dynamicdiceprototype.data

import androidx.compose.ui.graphics.Color
import com.example.dynamicdiceprototype.R
import kotlin.random.Random

class Configuration {
  val bundles: Map<String, List<String>> =
      mutableMapOf(
          "Kniffel" to
              listOf(
                  "6er",
                  "6er2",
                  "6er2",
                  "6er2",
                  "6er2",
                  "6er2",
                  "6er2",
                  "6er2",
                  "6er2",
                  "6er2",
                  "6er2",
                  "6er2",
                  "6er2",
                  "6er2",
                  "6er2"))
  val dices: MutableMap<String, Dice> =
      mutableMapOf(
          "random" to
              Dice(
                  name = "random",
                  faces =
                      listOf(
                          Face(imageId = "${R.drawable.three_transparent}"),
                          Face(imageId = "${R.drawable.rukaiya_rectangular}"))),
          "6er" to
              Dice(
                  name = "6er",
                  faces =
                      listOf(
                          Face(imageId = "${R.drawable.one_transparent}"),
                          Face(imageId = "${R.drawable.two_transparent}"),
                          Face(imageId = "${R.drawable.three_transparent}"),
                          Face(imageId = "${R.drawable.four_transparent}"),
                          Face(imageId = "${R.drawable.five_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"),
                          Face(imageId = "${R.drawable.six_transparent}"))))
  var lastBundle: String = "Kniffel"

  fun addDice(dice: Dice) {
    dices[dice.name] = dice
    // TODO Store config locally
  }

  fun setImages(images: Map<String, ImageModel>) {
    dices.forEach { it.value.faces.map { face -> face.data = images[face.imageId] } }
  }

  fun copyDice(name: String): Dice {
    val diceState = dices[name]
    if (diceState != null) {
      return copyIfNotExists(diceState.copy(name = name + "_copy"))
    }
    return Dice(
        faces = listOf()) // TODO Better handling of error, probably throw exception? Or return null
  }

  fun copyIfNotExists(dice: Dice): Dice {
    return if (dices.contains(dice.name)) copyIfNotExists(dice.copy(name = dice.name + "_copy"))
    else dice
  }

  data class Dice(
      val name: String = "diceName",
      val faces: List<Face>,
      var current: Face? = null,
      var state: DiceState = DiceState.UNLOCKED,
      var rotation: Float = ((Random.nextFloat() * (15) + 5) * if (Random.nextBoolean()) -1 else 1),
      var backgroundColor: Color = Color(0xFFCCCCCC)
  ) {

    init {
      if ((current === null || !faces.contains(current)) && faces.isNotEmpty()) current = roll()
    }

    fun roll(): Face {
      // always flip to the other side to show that the face is a new face
      rotation = ((Random.nextFloat() * (15) + 5) * if (rotation > 0) -1 else 1)
      return faces.random()
    }
  }

  enum class DiceState {
    LOCKED,
    UNLOCKED
  }
}
