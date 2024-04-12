package com.example.dynamicdiceprototype

data class Dice(var current: Layer, val layers: List<Layer>) {
    fun roll(): Layer {
        return layers.random()
    }
}