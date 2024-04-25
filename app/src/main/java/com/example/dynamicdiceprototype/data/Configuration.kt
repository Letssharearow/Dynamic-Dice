package com.example.dynamicdiceprototype.data

import com.example.dynamicdiceprototype.R

class Configuration {

  var configuration: Map<String, Map<String, Dice>> =
      hashMapOf(
          "Kniffel" to
              hashMapOf(
                  "6er" to
                      Dice(
                          layers =
                              listOf(
                                  Layer(imageId = "${R.drawable.rukaiya}"),
                                  Layer(imageId = "${R.drawable.rukaiya_rectangular}"))),
                  "6er2" to
                      Dice(
                          layers =
                              listOf(
                                  Layer(imageId = "${R.drawable.one_transparent}"),
                                  Layer(imageId = "${R.drawable.two_transparent}"),
                                  Layer(imageId = "${R.drawable.three_transparent}"),
                                  Layer(imageId = "${R.drawable.four_transparent}"),
                                  Layer(imageId = "${R.drawable.five_transparent}"),
                                  Layer(imageId = "${R.drawable.six_transparent}")))))
  var lastBundle: String = "Kniffel"
}
