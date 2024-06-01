package com.example.dynamicdiceprototype.utils

import getMaxGridWidth
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import selectNext

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

    maxGridWidth = getMaxGridWidth(count = 10, containerHeight = 650, containerWidth = 300)
    assertEquals(130f, maxGridWidth)
  }

  @org.junit.jupiter.api.Test
  @Test
  fun selectNextIndexSetFirstElementTest() {
    val list = listOf("first", "second", "third")
    val currentIndex = null
    val nextIndex = list.selectNext(currentIndex)
    assertEquals(0, nextIndex)
  }

  @org.junit.jupiter.api.Test
  @Test
  fun selectNextIndexSetNullIfListEmptyTest() {
    val list = emptyList<String>()
    val currentIndex = null
    val nextIndex = list.selectNext(currentIndex)
    assertEquals(null, nextIndex)
  }

  @org.junit.jupiter.api.Test
  @Test
  fun selectNextIndexSetNullIfLastIndexTest() {
    val list = listOf("first", "second", "third")
    val currentIndex = 2
    val nextIndex = list.selectNext(currentIndex)
    assertEquals(null, nextIndex)
  }

  @org.junit.jupiter.api.Test
  @Test
  fun selectNextIndexIncrementTest() {
    val list = listOf("first", "second", "third")
    val currentIndex = 1
    val nextIndex = list.selectNext(currentIndex)
    assertEquals(2, nextIndex)
  }
}
