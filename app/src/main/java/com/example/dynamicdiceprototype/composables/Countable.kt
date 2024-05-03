package com.example.dynamicdiceprototype.composables

interface Countable<Me : Countable<Me>> {
  fun getCount(): Int

  fun getId(): String

  fun copy(count: Int): Me
}
