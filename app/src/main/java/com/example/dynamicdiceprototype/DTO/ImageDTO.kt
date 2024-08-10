package com.example.dynamicdiceprototype.DTO

import kotlinx.serialization.Serializable

@Serializable
data class ImageDTO(
    var base64String: String = "",
    val contentDescription: String = "image",
    val tags: List<String> = listOf(),
) {
  override fun equals(other: Any?): Boolean {
    return this === other
  }

  override fun hashCode(): Int {
    var result = base64String.hashCode()
    result = 31 * result + contentDescription.hashCode()
    result = 31 * result + tags.hashCode()
    return result
  }
}
