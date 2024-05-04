package com.example.dynamicdiceprototype.composables.createdice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.dynamicdiceprototype.composables.ArrangedColumn
import com.example.dynamicdiceprototype.composables.ContinueButton
import com.example.dynamicdiceprototype.composables.FaceView
import com.example.dynamicdiceprototype.data.Face
import com.example.dynamicdiceprototype.services.DiceViewModel

@Composable
fun SelectFacesScreen2(viewModel: DiceViewModel, onFacesSelectionClick: () -> Unit) {
  val faces = remember {
    mutableStateMapOf(*viewModel.newDice.faces.map { Pair(it.imageId, it) }.toTypedArray())
  }

  val size = viewModel.facesSize
  val images = viewModel.imageMap

  ArrangedColumn {
    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.weight(1F)) {
      items(items = images.toList()) { (key, image) ->
        val matchingFace = faces[key]
        val imageIsSelected = matchingFace != null
        val weight = matchingFace?.weight?.toFloat() ?: 1F
        BoxWithConstraints(
            modifier =
                Modifier.clickable {
                  val face = faces[key]
                  if (face == null) faces[key] = Face(imageId = key, data = image)
                  else faces.remove((key))
                }) {
              val width = constraints.maxWidth
              val density = LocalDensity.current
              val maxWidthDp = with(density) { width.toDp() }

              if (imageIsSelected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Checked",
                    modifier = Modifier.align(Alignment.TopStart))
              }
              FaceView(
                  face = faces[key] ?: Face(data = image),
                  color = Color.Transparent,
                  spacing = maxWidthDp.div(10))
              if (imageIsSelected) {
                Slider(
                    value = weight,
                    onValueChange = { value ->
                      val face = faces[key]
                      if (face != null) faces[key] = face.copy(weight = value.toInt())
                    },
                    valueRange = 1f..size.toFloat(),
                    steps = size,
                    modifier =
                        Modifier.align(Alignment.BottomCenter)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5F)))
              }
            }
        // Display the image selection for each face
      }
    }
    ContinueButton(
        onClick = {
          viewModel.updateSelectedFaces(faces)
          onFacesSelectionClick()
        },
        text = "Next: (${faces.values.sumOf {it.weight}} / $size)")
  }
}
