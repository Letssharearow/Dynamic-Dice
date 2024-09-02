package com.example.dynamicdiceprototype.utils

import getMaxGridWidth
import getWeightsInRange
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import selectNext
import weightedRandom

class UtilsFunctionsKtTest {

  @Test
  fun getMaxGridWidthTestBiggerHeight() {
    var maxGridWidth = getMaxGridWidth(count = 10, containerHeight = 100, containerWidth = 10)
    assertEquals(10f, maxGridWidth)
  }

  @Test
  fun getMaxGridWidthTestBiggerWidth() {
    var maxGridWidth = getMaxGridWidth(count = 10, containerHeight = 10, containerWidth = 100)
    assertEquals(10f, maxGridWidth)
  }

  @Test
  fun getMaxGridWidthTestEqualHeightAndWidth() {
    var maxGridWidth = getMaxGridWidth(count = 9, containerHeight = 30, containerWidth = 30)
    assertEquals(10f, maxGridWidth)
  }

  @Test
  fun getMaxGridWidthTestNotFillFullWidth() {
    var maxGridWidth = getMaxGridWidth(count = 8, containerHeight = 80, containerWidth = 20)
    assertEquals(10f, maxGridWidth)
  }

  @Test
  fun getMaxGridWidthTestNotFillFullHeight() {
    var maxGridWidth = getMaxGridWidth(count = 8, containerHeight = 20, containerWidth = 80)
    assertEquals(10f, maxGridWidth)
  }

  @Test
  fun getMaxGridWidthTestStandardInput() {
    var maxGridWidth = getMaxGridWidth(count = 10, containerHeight = 650, containerWidth = 300)
    assertEquals(130f, maxGridWidth)
  }

  @Test
  fun selectNextIndexSetFirstElementTest() {
    val list = listOf("first", "second", "third")
    val currentIndex = null
    val nextIndex = list.selectNext(currentIndex)
    assertEquals(0, nextIndex)
  }

  @Test
  fun selectNextIndexSetNullIfListEmptyTest() {
    val list = emptyList<String>()
    val currentIndex = null
    val nextIndex = list.selectNext(currentIndex)
    assertEquals(null, nextIndex)
  }

  @Test
  fun selectNextIndexSetNullIfLastIndexTest() {
    val list = listOf("first", "second", "third")
    val currentIndex = 2
    val nextIndex = list.selectNext(currentIndex)
    assertEquals(null, nextIndex)
  }

  @Test
  fun selectNextIndexIncrementTest() {
    val list = listOf("first", "second", "third")
    val currentIndex = 1
    val nextIndex = list.selectNext(currentIndex)
    assertEquals(2, nextIndex)
  }

  @Test
  fun getRandomElementTest() {
    val list = listOf(0.0, 0.0, 0.0, 1.0)
    val result = weightedRandom(list)
    assertEquals(3, result)
  }

  @Test
  fun getRandomElementFirstTest() {
    val list = listOf(1.0, 0.0, 0.0, 0.0)
    val result = weightedRandom(list)
    assertEquals(0, result)
  }

  @Test
  fun getWeightsInRangeTest() {
    val result = getWeightsInRange(4, 6, 9, addLeadingZeros = false)
    assertEquals(listOf(0.25, 0.5, 1.0, 0.5, 0.25, 0.125), result)
  }

  @Test
  fun getWeightsInRangeCurveTest() {
    val result = getWeightsInRange(4, 6, 9, 1.0, addLeadingZeros = false)
    assertEquals(listOf(1.0, 1.0, 1.0, 1.0, 1.0, 1.0), result)
  }

  @Test
  fun getWeightsInRangeLeadingZerosTest() {
    val result = getWeightsInRange(4, 6, 9, 1.0, true)
    assertEquals(listOf(0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0), result)
  }
}
