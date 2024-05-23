package com.example.dynamicdiceprototype.composables.screens

import OneScreenGrid
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dynamicdiceprototype.composables.FaceView
import com.example.dynamicdiceprototype.data.Face
import com.example.dynamicdiceprototype.services.getFaces
import com.example.dynamicdiceprototype.services.mockImages
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TestScreen() {
  var size by remember { mutableIntStateOf(6) }
  Column {
    MultiShapedObject(size, 200.dp)
    Slider(
        value = size.toFloat(),
        onValueChange = { value -> size = value.toInt() },
        valueRange = 3f..20f,
        steps = 20)
  }
}

@Composable
fun MultiShapedObject(numSides: Int, size: Dp, color: Color = Color.Blue) {
  val radius = with(LocalDensity.current) { size.toPx() / 2 }
  val angle = 360f / numSides
  val path =
      Path().apply {
        moveTo(radius * 2, radius)
        for (i in 0 until numSides) {
          lineTo(
              (radius + radius * cos(i * angle * PI / 180f)).toFloat(),
              (radius + radius * sin(i * angle * PI / 180f)).toFloat())
        }
        close()
      }

      val faces = mockImages()
  Box(Modifier.size(size), contentAlignment = Alignment.Center) {
    Canvas(modifier = Modifier.size(size)) { drawPath(path, color) }
  Box(Modifier.size(size.div(2))) {
    FaceView(
        face = faces["five_transparent"], spacing = 2.dp)
  }
  }
}

@Composable
fun OneScreenGridTest() {
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

@Preview(showBackground = true)
@Composable
private fun Preview() {
  DynamicDicePrototypeTheme { TestScreen() }
}
