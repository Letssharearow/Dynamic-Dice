package com.example.dynamicdiceprototype.composables.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun ColorPicker(
    initialColor: Color,
    onColorChange: (color: ColorEnvelope) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
  val controller = rememberColorPickerController()

  Box {
    Column(
        modifier
            .padding(4.dp)
            .background(MaterialTheme.colorScheme.background)
            .border(BorderStroke(3.dp, MaterialTheme.colorScheme.secondary))) {
          HsvColorPicker(
              initialColor = initialColor,
              modifier = Modifier.weight(1F),
              controller = controller,
              onColorChanged = { onColorChange(it) })
          AlphaSlider(
              modifier = Modifier.fillMaxWidth().padding(4.dp).height(20.dp),
              initialColor = initialColor,
              controller = controller,
          )
          BrightnessSlider(
              modifier = Modifier.fillMaxWidth().padding(4.dp).height(20.dp),
              initialColor = initialColor,
              controller = controller,
          )
        }
    IconButton(onClick = onDismiss, Modifier.align(Alignment.TopEnd)) {
      Icon(imageVector = Icons.Filled.Clear, contentDescription = "Close Color Picker")
    }
  }
}
