package com.example.dynamicdiceprototype.composables.screens.dice

import OneScreenGrid
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dynamicdiceprototype.composables.common.ArrangedColumn
import com.example.dynamicdiceprototype.composables.common.ColorPicker
import com.example.dynamicdiceprototype.composables.common.SelectItemsGrid
import com.example.dynamicdiceprototype.composables.common.SingleLineTextInput
import com.example.dynamicdiceprototype.composables.common.TextButton
import com.example.dynamicdiceprototype.composables.screens.roll.FaceView
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.Face
import com.example.dynamicdiceprototype.services.PreferenceKey
import com.example.dynamicdiceprototype.services.PreferenceManager
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.DiceD6

@Composable
fun EditDiceScreen(
    dice: Dice,
    onSaveDice: (name: String, color: Color) -> Unit,
    onSaveWeights: (faceMap: Map<Face, Int>) -> Unit,
    onRerollDice: () -> Unit,
    showRerollButton: Boolean = false,
    onEditFaces: (name: String, color: Color) -> Unit
) {
  Log.d("EditDiceScreen", "die: $dice")
  var name by remember { mutableStateOf(dice.name) }
  var color by remember { mutableStateOf(dice.backgroundColor) }
  var isColorPickerOpen by remember { mutableStateOf(false) }
  val inPreviewMode = LocalInspectionMode.current
  if (inPreviewMode) PreferenceManager.init(context = LocalContext.current)
  val maxSize =
      PreferenceManager.getPreferenceFlow<Int>(PreferenceKey.ItemSelectionDiceWeightMaxSize)
          .collectAsState(initial = 500)
          .value

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
                  label = "Die Name",
                  isReadOnly = false,
                  isError = name.isEmpty(),
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
      var showAddWeights by remember { mutableStateOf(false) }
      if (showAddWeights) {
        SelectItemsGrid<Face>(
            selectables = dice.faces,
            onSaveSelection = { faceMap ->
              onSaveWeights(faceMap)
              showAddWeights = false
            },
            getId = { face -> face.contentDescription },
            modifier = Modifier.padding(0.dp),
            maxSize = maxSize,
            applyFilter = null,
            initialValue = dice.faces.associateWith { it.weight },
            view = { item, modifier, maxWidthDp, _ ->
              FaceView(
                  face = item,
                  spacing = maxWidthDp.div(10),
                  size = maxWidthDp.div(3),
                  modifier = modifier.fillMaxSize(),
                  showWeight = false,
                  color = color)
            },
            minValue = 1)
      } else {
        Box(Modifier.fillMaxSize()) {
          OneScreenGrid(items = dice.faces, minSize = if (isColorPickerOpen) 2000F else 200F) {
              item,
              maxWidth ->
            FaceView(
                face = item,
                showWeight = false,
                spacing = maxWidth.div(10),
                size =
                    maxWidth.div(
                        3), // TODO: get rid of this duplicate code, also in FaceSelectionScreen
                color = color,
                modifier = Modifier.padding(maxWidth.div(20)))
          }
          if (!isColorPickerOpen) {
            Column(
                modifier = Modifier.align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally) {
                  Row(
                      horizontalArrangement = Arrangement.SpaceAround,
                      verticalAlignment = Alignment.CenterVertically,
                  ) {
                    if (showRerollButton) {
                      Button(
                          onClick = {
                            onRerollDice()
                            name = dice.name
                            color = dice.backgroundColor
                          },
                          border =
                              BorderStroke(
                                  width = 2.dp,
                                  color = MaterialTheme.colorScheme.secondaryContainer),
                          colors =
                              ButtonDefaults.buttonColors(
                                  containerColor = MaterialTheme.colorScheme.secondary),
                          modifier = Modifier.height(48.dp)) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.DiceD6,
                                contentDescription = "random")
                          }
                    }
                    TextButton(onClick = { showAddWeights = true }, text = "Add weights")
                  }
                  Row(horizontalArrangement = Arrangement.SpaceAround) {
                    TextButton(
                        onClick = { if (name.isNotEmpty()) onEditFaces(name, color) },
                        text = "Edit Faces",
                        modifier = Modifier.padding(8.dp))
                    TextButton(
                        onClick = { if (name.isNotEmpty()) onSaveDice(name, color) },
                        text = "Save die",
                        modifier = Modifier.padding(8.dp))
                  }
                }
          }
        }
      }
    }
    if (isColorPickerOpen) {
      ColorPicker(
          initialColor = color,
          onColorChange = { color = if (it.fromUser) it.color else color },
          onDismiss = { isColorPickerOpen = false },
          modifier = Modifier.fillMaxHeight(0.4F))
    }
  }
}

@Preview
@Composable
private fun Preview() {
  DynamicDicePrototypeTheme {
    EditDiceScreen(
        dice =
            Dice(
                faces =
                    listOf(
                        Face(contentDescription = ""),
                        Face(contentDescription = ""),
                        Face(contentDescription = ""),
                        Face(contentDescription = ""))),
        onEditFaces = { a, b -> },
        onSaveWeights = { a -> },
        onRerollDice = {},
        onSaveDice = { a, b -> })
  }
}
