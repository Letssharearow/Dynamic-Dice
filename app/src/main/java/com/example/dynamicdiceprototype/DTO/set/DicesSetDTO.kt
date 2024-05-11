package com.example.dynamicdiceprototype.DTO.set

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.serialization.Serializable

@Serializable
data class DicesSetDTO(val dices: PersistentMap<String, DiceSetDTO> = persistentHashMapOf())
