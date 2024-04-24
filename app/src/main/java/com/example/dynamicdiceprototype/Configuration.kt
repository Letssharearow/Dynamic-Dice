package com.example.dynamicdiceprototype

class Configuration {

  var configuration: Map<String, Map<String, Dice>> =
      hashMapOf(
          "Kniffel" to
              hashMapOf(
                  "6er" to
                      Dice(
                          layers =
                              listOf(
                                  Layer("1", imageId = "${R.drawable.one_transparent}"),
                                  Layer("2", imageId = "${R.drawable.two_transparent}"),
                                  Layer("3", imageId = "${R.drawable.three_transparent}"),
                                  Layer("4", imageId = "${R.drawable.four_transparent}"),
                                  Layer("5", imageId = "${R.drawable.five_transparent}"),
                                  Layer("6", imageId = "${R.drawable.six_transparent}")))))
  var lastBundle: String = "Kniffel"
}
