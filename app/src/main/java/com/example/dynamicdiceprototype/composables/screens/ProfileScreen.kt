package com.example.dynamicdiceprototype.composables.screens

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onSettingsClick: () -> Unit) {
  Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
        // Profile Picture Placeholder
        Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(Color.Gray)) {
          Icon(
              imageVector = Icons.Default.Person,
              contentDescription = "Profile Picture",
              modifier = Modifier.align(Alignment.Center))
        }

        Button(
            onClick = onSettingsClick,
            modifier = Modifier.padding(top = 16.dp),
        ) {
          Text(text = "Settings", color = Color.White)
        }
      }
}
