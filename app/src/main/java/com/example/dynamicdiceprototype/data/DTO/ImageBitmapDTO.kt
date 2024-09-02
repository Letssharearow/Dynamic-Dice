package com.example.dynamicdiceprototype.data.DTO

import android.graphics.Bitmap
import com.example.dynamicdiceprototype.utils.ImageMapper

data class ImageBitmapDTO(
    var bitmap: Bitmap,
    val contentDescription: String = "image",
    val tags: List<String> = listOf(),
) {
  fun toImageDTO(): ImageDTO {
    return ImageDTO(
        contentDescription = contentDescription,
        tags = tags,
        base64String = ImageMapper.bitmapToBase64(bitmap))
  }
}
