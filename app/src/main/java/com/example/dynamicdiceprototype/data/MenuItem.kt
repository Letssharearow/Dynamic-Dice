package com.example.dynamicdiceprototype.data

data class AlterBoxProperties(
    val header: String = "Confirm Action",
    val description: String = "Press Confirm to confirm action",
)

data class MenuItem<T>(
    val text: String,
    val callBack: (T) -> Unit,
    val alert: AlterBoxProperties? = null
)
