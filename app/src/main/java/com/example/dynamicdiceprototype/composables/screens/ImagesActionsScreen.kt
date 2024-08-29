package com.example.dynamicdiceprototype.composables.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dynamicdiceprototype.DTO.ImageDTO

@Composable
fun ImagesActionsScreen(
    images: Map<ImageDTO, Int>,
    onDeleteImages: (Map<ImageDTO, Int>) -> Unit,
    onCreateDice: (Map<ImageDTO, Int>) -> Unit,
) {
  Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceEvenly) {
        // Profile Picture Placeholder

        Button(
            onClick = { onDeleteImages(images) },
            modifier = Modifier.padding(top = 16.dp),
        ) {
          Text(text = "Delete Images", color = Color.White)
        }

        Button(
            onClick = { onCreateDice(images) },
            modifier = Modifier.padding(top = 16.dp),
        ) {
          Text(text = "Create Dice", color = Color.White)
        }
      }
}
