package com.example.dynamicdiceprototype.utils

import com.example.dynamicdiceprototype.data.Face
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class RandomItemByWeightKtTest {

  @org.junit.jupiter.api.Test
  @Test
  fun testRandomItemByWeight100Percent(): Unit {
    val faces =
        listOf(
            Face(data = null, weight = 0, contentDescription = ""),
            Face(data = null, weight = 0, contentDescription = ""),
            Face(data = null, weight = 1, contentDescription = "weighted"),
            Face(data = null, weight = 0, contentDescription = ""),
            Face(data = null, weight = 0, contentDescription = ""),
            Face(data = null, weight = 0, contentDescription = ""),
            Face(data = null, weight = 0, contentDescription = ""),
            Face(data = null, weight = 0, contentDescription = ""),
            Face(data = null, weight = 0, contentDescription = ""),
            Face(data = null, weight = 0, contentDescription = ""))

    val randomItemByWeight = faces.randomItemByWeight()
    assertEquals("weighted", randomItemByWeight!!.contentDescription)
  }

  @Test
  fun testRandomItemByWeightStatistically(): Unit {
    val totalRuns = 10000
    val confidenceLevel = 3.891 // Z-Wert for 99,99% confidence intervalls
    var faces =
        mutableListOf(
            (Face(data = null, weight = 1, contentDescription = "face1_1000")),
            (Face(data = null, weight = 1, contentDescription = "face2_1000")),
            (Face(data = null, weight = 2, contentDescription = "face3_2000")),
            (Face(data = null, weight = 1, contentDescription = "face4_1000")),
            (Face(data = null, weight = 1, contentDescription = "face5_1000")),
            (Face(data = null, weight = 3, contentDescription = "face6_3000")),
            (Face(data = null, weight = 1, contentDescription = "face7_1000")))
    var results = mutableMapOf<Face, Int>()

    repeat(totalRuns) {
      val randomItemByWeight = faces.randomItemByWeight()!!
      results[randomItemByWeight] = (results[randomItemByWeight] ?: 0) + 1
    }

    val totalWeight = faces.sumOf { it.weight }

    println(results.toString())

    faces.forEachIndexed { _, face ->
      val expectedCount = (face.weight.toDouble() / totalWeight) * totalRuns

      val standardDeviation =
          kotlin.math.sqrt(totalRuns * expectedCount * (1 - expectedCount / totalRuns))
      val rangeForError =
          (confidenceLevel * standardDeviation / kotlin.math.sqrt(totalRuns.toDouble())).toInt()
      assertTrue(
          results[face] in
              (expectedCount - rangeForError).toInt()..(expectedCount + rangeForError).toInt())
    }
  }
}
