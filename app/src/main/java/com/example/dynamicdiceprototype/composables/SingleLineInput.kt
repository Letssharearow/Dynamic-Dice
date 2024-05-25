package com.example.dynamicdiceprototype.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SingleLineInput(
    text: String,
    onValueChange: (text: String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isReadOnly: Boolean = false,
    isError: Boolean = false,
) {
  OutlinedTextField(
      value = text,
      onValueChange = onValueChange,
      label = { Text(label) },
      singleLine = true,
      readOnly = isReadOnly,
      modifier = modifier.fillMaxWidth(),
      isError = isError,
  )
}
