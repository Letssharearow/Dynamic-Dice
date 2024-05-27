package com.example.dynamicdiceprototype.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SingleLineTextInput(
    text: String,
    onValueChange: (text: String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isReadOnly: Boolean = false,
    isError: Boolean = false,
    maxLength: Int = 50,
) {
  OutlinedTextField(
      value = text.coerceAtMost(""),
      onValueChange = { if (it.length <= maxLength) onValueChange(it) },
      label = { Text(label) },
      singleLine = true,
      readOnly = isReadOnly,
      modifier = modifier.fillMaxWidth(),
      isError = isError,
  )
}
