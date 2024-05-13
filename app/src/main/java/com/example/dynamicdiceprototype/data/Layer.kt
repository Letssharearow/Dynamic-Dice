package com.example.dynamicdiceprototype.data

import androidx.compose.ui.graphics.ImageBitmap

data class Face(
    var data: ImageBitmap? = null,
    val contentDescription: String,
    var weight: Int = 1
) {
  override fun toString(): String {
    return contentDescription
  }
}
