package com.example.dynamicdiceprototype.services

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

object HeaderViewModel : ViewModel() {
  var headerText by mutableStateOf("Main Screen")

  fun changeHeaderText(text: String) {
    headerText = text
  }
}
