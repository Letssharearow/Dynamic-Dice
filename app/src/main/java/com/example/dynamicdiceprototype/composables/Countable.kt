package com.example.dynamicdiceprototype.composables

interface Copyable<out T> where T : Copyable<T> {

  fun copy(count: Int): T
}
