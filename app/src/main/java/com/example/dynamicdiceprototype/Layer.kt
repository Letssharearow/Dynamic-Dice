package com.example.dynamicdiceprototype

data class Layer(val data: String, val weight: Int = 1) {
    override fun toString(): String {
        return data
    }
}