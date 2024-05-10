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
                        Face(imageId = "${R.drawable.six_transparent}"))))
