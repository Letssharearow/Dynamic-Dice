package com.example.dynamicdiceprototype.composables.createdice

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.example.dynamicdiceprototype.DTO.ImageDTO
import com.example.dynamicdiceprototype.composables.FaceView
import com.example.dynamicdiceprototype.composables.SelectItemsGrid
import com.example.dynamicdiceprototype.data.Face
import com.example.dynamicdiceprototype.services.FirebaseDataStore
import com.example.dynamicdiceprototype.services.PreferenceKey
import com.example.dynamicdiceprototype.services.PreferenceManager
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

@Composable
fun SelectFacesScreen(
    faces: List<ImageDTO>,
    initialValue: Map<ImageDTO, Int>,
    color: Color = Color.Gray,
    onFacesSelectionClick: (Map<ImageDTO, Int>) -> Unit
) {
  val maxSize =
      PreferenceManager.getPreferenceFlow<Int>(PreferenceKey.ItemSelectionDiceValueMaxSize)
          .collectAsState(initial = 5000)
          .value
  var mutableFaces by remember {
    mutableStateOf(
        faces
            .toMutableList()
            .apply { add(0, ImageDTO(contentDescription = "number", base64String = "number")) }
            .toList())
  }
  SelectItemsGrid<ImageDTO>(
      selectables = mutableFaces,
      onSaveSelection = onFacesSelectionClick,
      getId = { face -> face.contentDescription },
      maxSize = maxSize,
      initialValue = initialValue,
      applyFilter = { image, filter ->
        image.contentDescription.contains(filter, ignoreCase = true) ||
            image.tags.find { it.contains(filter, ignoreCase = true) } != null
      },
      handleItemClick = { image ->
        if (image == mutableFaces.first()) {
          val mutableList = mutableFaces.toMutableList()
          mutableList.add(
              0,
              ImageDTO(
                  contentDescription = image.contentDescription,
                  tags = image.tags,
                  base64String = image.base64String))
          mutableFaces = mutableList
        }
      }) { item, modifier, maxWidthDp ->
        FaceView(
            face =
                Face(
                    contentDescription = item.contentDescription,
                    data = FirebaseDataStore.base64ToBitmap(item.base64String)),
            spacing = maxWidthDp.div(10),
            size = maxWidthDp.div(3),
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
