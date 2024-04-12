package com.example.dynamicdiceprototype

data class Dice(var current: Layer, val layers: List<Layer>) {
    fun roll(): Layer {
        val random = layers.random()
        current = random
        return random
    }
}