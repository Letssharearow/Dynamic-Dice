package com.example.dynamicdiceprototype

data class Dice(val layers: List<Layer>) {
    fun roll(): Layer {
        return layers.random()
    }
}