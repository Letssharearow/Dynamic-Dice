package com.example.dynamicdiceprototype.DTO

import android.graphics.Bitmap
import com.example.dynamicdiceprototype.services.FirebaseDataStore

data class ImageBitmapDTO(
    var bitmap: Bitmap,
    val contentDescription: String = "image",
    val tags: List<String> = listOf(),
) {
  fun toImageDTO(): ImageDTO {
    return ImageDTO(
        contentDescription = contentDescription,
        tags = tags,
        base64String = FirebaseDataStore.bitmapToBase64(bitmap))
  }
}
