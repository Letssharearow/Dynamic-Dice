package com.example.dynamicdiceprototype.composables

import OneScreenGrid
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dynamicdiceprototype.services.getFaces

@Composable
fun TestScreen() {
  Box(Modifier.height(400.dp).width(300.dp)) {
    Column {
      Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(Color.Cyan))
      OneScreenGrid(items = getFaces(10), minSize = 30f, Modifier.weight(1f)) { item, maxWidth ->
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                androidx.compose.ui.Modifier.height(maxWidth).width(maxWidth).padding(4.dp)) {
              FaceView(face = item, spacing = maxWidth.div(10), color = Color.Black)
              Text(text = "$maxWidth", Modifier.background(Color.White))
            }
      }

      Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(Color.Cyan))
    }
  }
}
