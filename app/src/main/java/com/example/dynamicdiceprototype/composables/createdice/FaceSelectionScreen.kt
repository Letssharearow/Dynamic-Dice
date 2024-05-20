package com.example.dynamicdiceprototype.composables.createdice

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.example.dynamicdiceprototype.composables.FaceView
import com.example.dynamicdiceprototype.composables.SelectItemsGrid
import com.example.dynamicdiceprototype.data.Face
import com.example.dynamicdiceprototype.services.getFaces
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

@Composable
fun SelectFacesScreen(
    faces: List<Face>,
    initialValue: Map<String, Face>,
    size: Int,
    onFacesSelectionClick: (Map<String, Face>) -> Unit
) {
  SelectItemsGrid<Face>(
      selectables = faces,
      initialSize = size,
      initialValue = initialValue,
      onSaveSelection = { map -> onFacesSelectionClick(map) },
      getCount = { face -> face.weight },
      copy = { face, count -> face.copy(weight = count) },
      getId = { face -> face.contentDescription }) { face, modifier, maxWidthDp ->
        FaceView(face = face, spacing = maxWidthDp.div(10), modifier = modifier.fillMaxSize())
      }
}

@Preview(device = Devices.PIXEL_TABLET)
@Composable
private fun SelectFacesScreenPreview() {
  DynamicDicePrototypeTheme {
    SelectFacesScreen(faces = getFaces(30), size = 6, initialValue = mapOf()) {}
  }
}
