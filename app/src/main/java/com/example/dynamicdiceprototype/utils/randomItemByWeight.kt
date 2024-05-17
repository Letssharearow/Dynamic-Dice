package com.example.dynamicdiceprototype.utils

import com.example.dynamicdiceprototype.data.Weightable

fun <E> List<E>.randomItemByWeight(): E? where E : Weightable {
  if (isEmpty()) return null
  val totalWeight = sumOf { it.getItemWeight() }
  var randomWeight = (1..totalWeight).random()
  for (item in this) {
    randomWeight -= (item.getItemWeight())
    if (randomWeight <= 0) return item
  }
  return null
}
