package com.example.dynamicdiceprototype.data

data class DiceState(
    val dice: String,
    val locked: Boolean = false,
    val faceName: String?,
    val value: Int? = null,
    val state: String? = null
)

data class RollState(
    val unlockedDicesCount: Int,
    val rollId: Int = 0,
    val sum: Int = 0,
    val diceStates: List<DiceState> = listOf(),
    val isEmpty: Boolean = false,
)
