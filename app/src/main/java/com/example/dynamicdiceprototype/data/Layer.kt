package com.example.dynamicdiceprototype.data

import androidx.compose.ui.graphics.ImageBitmap

data class Layer(val data: ImageBitmap? = null, val imageId: String = "1", val weight: Int = 1) {
  override fun toString(): String {
    return imageId
  }
}
