package com.example.dynamicdiceprototype.DTO.set

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

@Serializable
data class UserSetDTO(
    val dices: PersistentList<String> = persistentListOf(),
    val diceGroups: PersistentMap<String, PersistentMap<String, Int>> = persistentHashMapOf()
)
