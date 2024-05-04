package com.example.dynamicdiceprototype.utils

import getMaxGridWidth
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class UtilsKtTest {

  @org.junit.jupiter.api.Test
  @Test
  fun getMaxGridWidthTest() {
    var maxGridWidth = getMaxGridWidth(count = 10, containerHeight = 100, containerWidth = 10)
    assertEquals(10f, maxGridWidth)

    maxGridWidth = getMaxGridWidth(count = 10, containerHeight = 10, containerWidth = 100)
    assertEquals(10f, maxGridWidth)

    maxGridWidth = getMaxGridWidth(count = 9, containerHeight = 30, containerWidth = 30)
    assertEquals(10f, maxGridWidth)

    maxGridWidth = getMaxGridWidth(count = 8, containerHeight = 80, containerWidth = 20)
    assertEquals(10f, maxGridWidth)

    maxGridWidth = getMaxGridWidth(count = 8, containerHeight = 20, containerWidth = 80)
    assertEquals(10f, maxGridWidth)
  }
}
