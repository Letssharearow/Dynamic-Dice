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
                  layers =
                      listOf(
                          Layer(imageId = "${R.drawable.three_transparent}"),
                          Layer(imageId = "${R.drawable.rukaiya_rectangular}"))),
          "6er2" to
              Dice(
                  name = "6er",
                  layers =
                      listOf(
                          Layer(imageId = "${R.drawable.one_transparent}"),
                          Layer(imageId = "${R.drawable.two_transparent}"),
                          Layer(imageId = "${R.drawable.three_transparent}"),
                          Layer(imageId = "${R.drawable.four_transparent}"),
                          Layer(imageId = "${R.drawable.five_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"),
                          Layer(imageId = "${R.drawable.six_transparent}"))))
  var lastBundle: String = "Kniffel"
}
