package com.example.dynamicdiceprototype.data

data class Layer(val data: ImageModel? = null, val imageId: String = "1", var weight: Int = 1) {
  override fun toString(): String {
    return imageId
  }
}