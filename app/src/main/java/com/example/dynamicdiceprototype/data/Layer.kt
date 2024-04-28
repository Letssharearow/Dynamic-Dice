package com.example.dynamicdiceprototype.data

data class Face(var data: ImageModel? = null, val imageId: String = "1", var weight: Int = 1) {
  override fun toString(): String {
    return imageId
  }
}
