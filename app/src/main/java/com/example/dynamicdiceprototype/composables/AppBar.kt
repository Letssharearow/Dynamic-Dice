package com.example.dynamicdiceprototype.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dynamicdiceprototype.services.view_model.HeaderViewModel
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(onMenuClicked: () -> Unit, onProfileClicked: () -> Unit) {
  val viewModel = viewModel<HeaderViewModel>()
  TopAppBar(
      title = { Text(text = viewModel.headerText) },
      navigationIcon = {
        IconButton(onClick = { onMenuClicked() }) {
          Icon(Icons.Filled.Menu, contentDescription = "Menu")
        }
      },
      actions = {
        IconButton(onClick = onProfileClicked) {
          Icon(Icons.Filled.AccountCircle, contentDescription = "Profile")
        }
      })
}

@Preview(showBackground = true)
@Composable
fun AppBarPreview() {
  DynamicDicePrototypeTheme {
    AppBar({ /* Handle navigation icon click */ }) { /* Handle profile picture button click */ }
  }
}
