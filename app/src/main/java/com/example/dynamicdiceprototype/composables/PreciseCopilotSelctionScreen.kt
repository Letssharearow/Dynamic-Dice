// package com.example.dynamicdiceprototype.composables
//
// import androidx.compose.foundation.background
// import androidx.compose.foundation.clickable
// import androidx.compose.foundation.layout.BoxWithConstraints
// import androidx.compose.foundation.layout.padding
// import androidx.compose.foundation.lazy.grid.GridCells
// import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
// import androidx.compose.foundation.lazy.grid.items
// import androidx.compose.foundation.shape.RoundedCornerShape
// import androidx.compose.material.icons.Icons
// import androidx.compose.material.icons.filled.Check
// import androidx.compose.material3.Icon
// import androidx.compose.material3.MaterialTheme
// import androidx.compose.material3.Slider
// import androidx.compose.runtime.Composable
// import androidx.compose.runtime.mutableStateMapOf
// import androidx.compose.runtime.remember
// import androidx.compose.ui.Alignment
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.draw.clip
// import androidx.compose.ui.platform.LocalDensity
// import androidx.compose.ui.unit.dp
// import androidx.lifecycle.ViewModel
// import androidx.room.util.copy
//
// @Composable
// fun <T> SelectionScreen(
//    items: Map<String, List<T>>,
//    viewModel: ViewModel,
//    view: @Composable (T) -> Unit,
//    onClick: (String, T?) -> Unit
// ) {
//  val selectedItems = remember {
//    mutableStateMapOf(*items.map { Pair(it.key, it.value) }.toTypedArray())
//  }
//
//  val size = viewModel.size
//
//  ArrangedColumn {
//    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.weight(1F)) {
//      items(items.values.toList()) { (key, item) ->
//        val matchingItem = selectedItems[key]
//        val itemIsSelected = matchingItem != null
//        val weight = matchingItem?.weight?.toFloat() ?: 1F
//        BoxWithConstraints(modifier = Modifier.clickable { onClick(key, selectedItems[key]) }) {
//          val width = constraints.maxWidth
//          val density = LocalDensity.current
//          val maxWidthDp = with(density) { width.toDp() }
//
//          if (itemIsSelected) {
//            Icon(
//                imageVector = Icons.Filled.Check,
//                contentDescription = "Checked",
//                modifier = Modifier.align(Alignment.TopStart))
//          }
//          view(item)
//          if (itemIsSelected) {
//            Slider(
//                value = weight,
//                onValueChange = { value ->
//                  val item = selectedItems[key]
//                  if (item != null) selectedItems[key] = item.copy(weight = value.toInt())
//                },
//                valueRange = 1f..size.toFloat(),
//                steps = size,
//                modifier =
//                    Modifier.align(Alignment.BottomCenter)
//                        .padding(8.dp)
//                        .clip(RoundedCornerShape(12.dp))
//                        .background(
//                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5F)))
//          }
//        }
//      }
//    }
//    ContinueButton(
//        onClick = {
//          viewModel.updateSelectedItems(selectedItems)
//          onFacesSelectionClick()
//        },
//        text = "Next: (${selectedItems.values.sumOf {it.weight}} / $size)")
//  }
// }
