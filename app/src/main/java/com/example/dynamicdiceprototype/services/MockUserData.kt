package com.example.dynamicdiceprototype.services

import androidx.compose.runtime.mutableStateMapOf
import com.example.dynamicdiceprototype.R
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.Face

fun getMockDiceGroups() =
    mutableStateMapOf(
        "Kniffel" to
            listOf(
                Pair("6er", 5),
            ))

fun getMockDices() =
    mutableStateMapOf(
        "random" to
            Dice(
                name = "random",
                faces =
                    listOf(
                        Face(contentDescription = "${R.drawable.three_transparent}"),
                        Face(contentDescription = "${R.drawable.rukaiya_rectangular}"))),
        "6er" to
            Dice(
                name = "6er",
                faces =
                    listOf(
                        Face(contentDescription = "${R.drawable.one_transparent}"),
                        Face(contentDescription = "${R.drawable.two_transparent}"),
                        Face(contentDescription = "${R.drawable.three_transparent}"),
                        Face(contentDescription = "${R.drawable.four_transparent}"),
                        Face(contentDescription = "${R.drawable.five_transparent}"),
                        Face(contentDescription = "${R.drawable.six_transparent}"))))

fun getFaces(n: Int): List<Face> {
  val list = mutableListOf<Face>()
  for (i in 1..n) {
    list.add(Face(contentDescription = "${R.drawable.six_transparent}"))
  }
  return list
}

fun getDices(n: Int = 5): List<Dice> {
  val list = mutableListOf<Dice>()
  for (i in 1..n) {
    list.add(
        Dice(
            name = "6er",
            faces =
                listOf(
                    Face(contentDescription = "${R.drawable.one_transparent}"),
                    Face(contentDescription = "${R.drawable.two_transparent}"),
                    Face(contentDescription = "${R.drawable.three_transparent}"),
                    Face(contentDescription = "${R.drawable.four_transparent}"),
                    Face(contentDescription = "${R.drawable.five_transparent}"),
                    Face(contentDescription = "${R.drawable.six_transparent}"))))
  }
  return list
}
