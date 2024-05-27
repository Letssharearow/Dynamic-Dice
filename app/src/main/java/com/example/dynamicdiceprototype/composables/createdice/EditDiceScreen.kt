package com.example.dynamicdiceprototype.composables.createdice

import OneScreenGrid
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dynamicdiceprototype.composables.FaceView
import com.example.dynamicdiceprototype.composables.SingleLineTextInput
import com.example.dynamicdiceprototype.composables.common.ArrangedColumn
import com.example.dynamicdiceprototype.composables.common.ColorPicker
import com.example.dynamicdiceprototype.composables.common.ContinueButton
import com.example.dynamicdiceprototype.data.Dice

@Composable
fun EditDiceScreen(
    dice: Dice,
    isEditMode: Boolean,
    onSaveDice: (name: String, color: Color) -> Unit,
    onEdit: (name: String, color: Color) -> Unit
) {
  var name by remember { mutableStateOf(dice.name) }
  var color by remember { mutableStateOf(dice.backgroundColor) }
  var isColorPickerOpen by remember { mutableStateOf(false) }

  ArrangedColumn {
    // color picker
    if (!isColorPickerOpen) {
      Row(
          Modifier.fillMaxWidth()
              .height(
                  IntrinsicSize
                      .Max)) { // TODO fix: if the name is too long the height of this row changes
            Box {
              SingleLineTextInput(
                  text = name,
                  onValueChange = { name = it },
                  label = "Dice Name",
                  isReadOnly = isEditMode,
                  modifier = Modifier.padding(8.dp).fillMaxWidth(0.5F))
            }
            Box(
                modifier =
                    Modifier.fillMaxWidth()
                        .fillMaxHeight()
                        .padding(8.dp)
                        .background(color)
                        .clickable { isColorPickerOpen = !isColorPickerOpen }) {
                  Text(
                      text = if (isColorPickerOpen) "Close Color Picker" else "Change Color",
                      Modifier.align(Alignment.Center))
                }
          }
    }
    Box(modifier = Modifier.weight(1F)) {
      OneScreenGrid(items = dice.faces, minSize = if (isColorPickerOpen) 2000F else 200F) {
          item,
          maxWidth ->
        FaceView(
            face = item,
            showWeight = true,
            spacing = maxWidth.div(10),
            color = color,
            modifier = Modifier.padding(maxWidth.div(20)))
      }
    }
    if (isColorPickerOpen) {
      ColorPicker(
          initialColor = color,
          onColorChange = { color = if (it.fromUser) it.color else color },
          onDismiss = { isColorPickerOpen = false },
          modifier = Modifier.fillMaxHeight(0.4F))
    }
    Row(horizontalArrangement = Arrangement.SpaceAround) {
      ContinueButton(onClick = { onEdit(name, color) }, text = "Edit Faces")
      ContinueButton(onClick = { onSaveDice(name, color) }, text = "Save Dice")
    }
  }
}
