package com.example.dynamicdiceprototype.DTO

import kotlinx.serialization.Serializable

@Serializable
data class ImageDTO(
    var base64String: String = "",
    val contentDescription: String = "image",
    val tags: List<String> = listOf(),
)
