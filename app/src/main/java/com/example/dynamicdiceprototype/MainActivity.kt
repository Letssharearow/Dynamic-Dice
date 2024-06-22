package com.example.dynamicdiceprototype

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.unit.dp
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      DynamicDicePrototypeTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          SimpleFilledTextFieldSample()
        }
      }
    }
  }
}

@Composable
fun SimpleFilledTextFieldSample() {
  var text by remember { mutableStateOf("Hello") }

  TextField(
      value = text,
      onValueChange = {
        text = it
        Log.d("TextField", "Text field value changed: $text")
      },
      label = { Text("Label") },
      singleLine = true,
      modifier =
          Modifier.padding(24.dp).onFocusEvent { event ->
            if (event.isFocused) {
              Log.d("TextField", "Text field gained focus")
            } else {
              Log.d("TextField", "Text field lost focus")
            }
          })
}
