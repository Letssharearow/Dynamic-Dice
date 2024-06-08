package com.example.dynamicdiceprototype.composables.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dynamicdiceprototype.services.USER
import com.example.dynamicdiceprototype.services.USER_FETCH

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onSettingsClick: () -> Unit) {
          Column(
            modifier = Modifier.fillMaxSize(),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center) {
                // Profile Picture Placeholder
                Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(Color.Gray))

                // User Information
                Text(
                    text = "User Information",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black)

                Text(text = "fetch: $USER_FETCH", fontSize = 16.sp, color = Color.Gray)
                Text(text = "write: $USER", fontSize = 16.sp, color = Color.Gray)

                // Settings Button
                Button(
                    onClick = onSettingsClick,
                    modifier = Modifier.padding(top = 16.dp),) {
                      Text(text = "Settings", color = Color.White)
                    }
              }

}
