package com.example.dynamicdiceprototype.DTO

import kotlinx.serialization.Serializable

@Serializable
data class FaceDTO(
    val contentDescription: String = "image",
    val weight: Int = 1,
    val value: Int = 1,
)