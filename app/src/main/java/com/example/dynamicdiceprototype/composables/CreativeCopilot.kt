// package com.example.dynamicdiceprototype.composables
//
// import androidx.compose.foundation.clickable
// import androidx.compose.foundation.layout.BoxWithConstraints
// import androidx.compose.foundation.lazy.grid.GridCells
// import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
// import androidx.compose.runtime.Composable
// import androidx.compose.runtime.mutableStateMapOf
// import androidx.compose.runtime.remember
// import androidx.compose.ui.Modifier
// import com.example.dynamicdiceprototype.data.Face
// import com.example.dynamicdiceprototype.services.DiceViewModel
//
// @Composable
// fun <T> SelectionScreenCC(
//    items: List<T>,
//    selectedItemMap: MutableMap<Int, T>,
//    itemView: @Composable (T, Boolean) -> Unit,
//    onSelectionClick: () -> Unit,
//    size: Int
// ) {
//  LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.weight(1F)) {
//    items(items = items) { item ->
//      val key = item.hashCode() // Assuming a unique hash for each item
//      val isSelected = selectedItemMap.containsKey(key)
//      val weight = (selectedItemMap[key] as? Face)?.weight?.toFloat() ?: 1F
//      BoxWithConstraints(
//          modifier =
//              Modifier.clickable {
//                if (!isSelected) selectedItemMap[key] = item else selectedItemMap.remove(key)
//              }) {
//            itemView(item, isSelected)
//            if (isSelected) {
//              // Slider logic here
//            }
//          }
//    }
//  }
//  ContinueButton(
//      onClick = { onSelectionClick() },
//      text = "Next: (${selectedItemMap.values.sumOf { (it as? Face)?.weight ?: 1 }} / $size)")
// }
//
// @Composable
// fun SelectFacesScreenCC(viewModel: DiceViewModel, onFacesSelectionClick: () -> Unit) {
//  val faces = remember {
//    mutableStateMapOf(*viewModel.dice.faces.map { Pair(it.hashCode(), it) }.toTypedArray())
//  }
//  val size = viewModel.facesSize
//  val images = viewModel.imageMap
//
//  SelectionScreenCC(
//      items = images.values.toList(),
//      selectedItemMap = faces,
//      itemView = { face, isSelected ->
//        // Your FaceView composable here
//      },
//      onSelectionClick = {
//        viewModel.updateSelectedFaces(faces)
//        onFacesSelectionClick()
//      },
//      size = size)
// }
//
// @Composable
// fun SelectDiceScreenCC(viewModel: DiceViewModel, onDiceSelectionClick: () -> Unit) {
//  val dice = remember {
//    mutableStateMapOf(*viewModel.diceGroup.dice.map { Pair(it.hashCode(), it) }.toTypedArray())
//  }
//  val size = viewModel.diceSize
//  val diceList = viewModel.diceGroup.dice
//
//  SelectionScreenCC(
//      items = diceList,
//      selectedItemMap = dice,
//      itemView = { die, isSelected ->
//        // Your DiceCard composable here
//      },
//      onSelectionClick = {
//        viewModel.updateSelectedDice(dice)
//        onDiceSelectionClick()
//      },
//      size = size)
// }
