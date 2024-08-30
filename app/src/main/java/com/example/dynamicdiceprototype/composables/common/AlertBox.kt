package com.example.dynamicdiceprototype.composables.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.dynamicdiceprototype.data.AlterBoxProperties
import com.example.dynamicdiceprototype.data.MenuItem

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

@Composable
fun <T> PopMenuWithAlert(
    actionItem: T,
    items: List<MenuItem<T>>,
    showMenu: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {

  var alertProps by remember { mutableStateOf<Pair<(T) -> Unit, AlterBoxProperties>?>(null) }

  DropdownMenu(expanded = showMenu, onDismissRequest = { onDismiss() }, modifier = modifier) {
    items.forEach { item ->
      DropdownMenuItem(
          text = { Text(text = item.text) },
          onClick = {
            onDismiss()
            if (item.alert != null) {
              alertProps = Pair(item.callBack, item.alert)
            } else {
              item.callBack(actionItem)
            }
          })
    }
  }
  alertProps?.let {
    AlertDialog(
        onDismissRequest = { alertProps = null },
        title = { Text(it.second.header) },
        text = { Text(it.second.description) },
        confirmButton = { Button(onClick = { it.first(actionItem) }) { Text("Confirm") } },
        dismissButton = { Button(onClick = { alertProps = null }) { Text("Cancel") } })
  }
}
