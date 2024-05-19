package com.example.dynamicdiceprototype.composables.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun AlertBox(
    title: String = "Confirm Action",
    text: String,
    isOpen: Boolean,
    onDismiss: () -> Unit,
    conConfirm: () -> Unit
) {
  if (isOpen) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = { Button(onClick = conConfirm) { Text("Confirm") } },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancel") } })
  }
}
