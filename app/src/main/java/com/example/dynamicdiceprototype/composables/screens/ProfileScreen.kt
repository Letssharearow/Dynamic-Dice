package com.example.dynamicdiceprototype.composables.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.dynamicdiceprototype.services.USER
import com.example.dynamicdiceprototype.services.USER_FETCH

@Composable
fun ProfileScreen() {
  Box(Modifier.fillMaxSize()) {
    Column (modifier = Modifier.align(Alignment.Center)){
      Text(text = "fetch: $USER_FETCH", )
      Text(text = "write: $USER", )
    }
  }
}
