package com.example.dynamicdiceprototype.services.view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

object HeaderViewModel : ViewModel() {
  var headerText by mutableStateOf("Roll")

  fun changeHeaderText(text: String) {
    headerText = text
  }
}
