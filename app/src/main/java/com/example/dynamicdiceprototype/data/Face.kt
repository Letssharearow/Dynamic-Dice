package com.example.dynamicdiceprototype.data

import androidx.compose.ui.graphics.ImageBitmap

interface Weightable {
  fun getItemWeight(): Int
}

data class Face(
    var data: ImageBitmap? = null,
    val contentDescription: String,
    var weight: Int = 1,
    var value: Int = 1,
) : Weightable {
  override fun toString(): String {
    return contentDescription
  }

  override fun getItemWeight(): Int {
    return weight
  }
}
