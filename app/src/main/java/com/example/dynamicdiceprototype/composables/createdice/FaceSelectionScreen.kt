package com.example.dynamicdiceprototype.composables.createdice

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.example.dynamicdiceprototype.DTO.ImageDTO
import com.example.dynamicdiceprototype.composables.FaceView
import com.example.dynamicdiceprototype.composables.SelectItemsGrid
import com.example.dynamicdiceprototype.data.Face
import com.example.dynamicdiceprototype.services.FirebaseDataStore
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

@Composable
fun SelectFacesScreen(
    faces: List<ImageDTO>,
    initialValue: Map<ImageDTO, Int>,
    color: Color = Color.Gray,
    onFacesSelectionClick: (Map<ImageDTO, Int>) -> Unit
) {
  SelectItemsGrid<ImageDTO>(
      selectables = faces,
      onSaveSelection = onFacesSelectionClick,
      getId = { face -> face.contentDescription },
      maxSize = 20,
      initialValue = initialValue,
      applyFilter = { image, filter ->
        image.contentDescription.contains(filter) || image.tags.find { it.contains(filter) } != null
      }) { item, modifier, maxWidthDp ->
        FaceView(
            face =
                Face(
                    contentDescription = item.contentDescription,
                    data = FirebaseDataStore.base64ToBitmap(item.base64String)),
            spacing = maxWidthDp.div(10),
            modifier = modifier.fillMaxSize(),
            color = color)
      }
}

@Preview(device = Devices.PIXEL_TABLET)
@Composable
private fun SelectFacesScreenPreview() {
  DynamicDicePrototypeTheme {
    SelectFacesScreen(faces = listOf(ImageDTO()), initialValue = mapOf()) {}
  }
}
