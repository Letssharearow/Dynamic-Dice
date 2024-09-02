package com.example.dynamicdiceprototype.composables.common

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun TextButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
  Button(modifier = modifier, onClick = onClick, enabled = enabled) { Text(text, fontSize = 24.sp) }
}
