package com.example.dynamicdiceprototype.data

import com.example.dynamicdiceprototype.R

class Configuration {

  val bundles: Map<String, List<String>> =
      hashMapOf(
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
  val dices: Map<String, Dice> =
      hashMapOf(
          "6er" to
              Dice(
                  name = "random",
                  faces =
                      listOf(
                          Face(imageId = "${R.drawable.three_transparent}"),
                          Face(imageId = "${R.drawable.rukaiya_rectangular}"))),
          "6er2" to
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
}
