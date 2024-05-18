package com.example.dynamicdiceprototype.data

data class AlterBoxProperties(
    val header: String = "Confirm Action",
    val description: String = "Press Confirm to confirm action",
    val callBack: () -> Unit
)

data class MenuItem(
    val text: String,
    val callBack: () -> Unit,
    val alter: AlterBoxProperties? = null
)
