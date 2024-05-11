package com.example.dynamicdiceprototype.DTO.set

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.serialization.Serializable

@Serializable
data class DiceSetDTO(
    val images: PersistentMap<String, Int> = persistentHashMapOf(),
    var backgroundColor: Int = Color(0xFFCCCCCC).toArgb()
    // TODO add userId to ensure only the user can modify his dice
)
