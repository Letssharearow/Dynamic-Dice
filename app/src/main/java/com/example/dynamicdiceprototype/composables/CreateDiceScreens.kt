package com.example.dynamicdiceprototype.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dynamicdiceprototype.Screen
import com.example.dynamicdiceprototype.data.ImageModel
import com.example.dynamicdiceprototype.data.Layer
import com.example.dynamicdiceprototype.services.DiceCreationViewModel
import com.example.dynamicdiceprototype.services.DiceViewModel

@Composable
fun CreateDiceNavGraph(imagesViewModel: DiceViewModel) {
  val viewModel = DiceCreationViewModel(imagesViewModel.configuration.dices)

  val navController = rememberNavController()

  NavHost(navController, startDestination = Screen.SelectLayers.route) {
    composable(route = Screen.Templates.route) {
      TemplateSelectionScreen(viewModel) { navController.navigate(Screen.CreateNewTemplate.route) }
    }
    composable(route = Screen.CreateNewTemplate.route) {
      TemplateCreationScreen(viewModel) { navController.navigate(Screen.SelectLayers.route) }
    }
    composable(route = Screen.SelectLayers.route) {
      SelectLayersScreen(viewModel, imagesViewModel) {
        navController.navigate(Screen.EditTemplate.route)
      }
    }
    composable(route = Screen.EditTemplate.route) {
      EditTemplateScreen(viewModel) { navController.navigate(Screen.EditTemplate.route) }
    }
  }
}

@Composable
fun TemplateSelectionScreen(viewModel: DiceCreationViewModel, onCreateNewDice: () -> Unit) {

  Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.SpaceBetween,
      horizontalAlignment = Alignment.CenterHorizontally) {
        //
        LazyColumn() {
          items(viewModel.templates) { template ->
            // Display each template and handle selection
            Button(onClick = onCreateNewDice, modifier = Modifier.padding(top = 16.dp)) {
              Text(template.name)
            }
          }
        }

        Button(
            onClick = onCreateNewDice, modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)) {
              Text("Create New Dice")
            }
      }
}

@Composable
fun TemplateCreationScreen(viewModel: DiceCreationViewModel, onCreateNewDice: () -> Unit) {

  var name by remember { mutableStateOf("") }
  var numLayers by remember { mutableStateOf("") }
  // Display the list of templates
  // TODO Add some cool pictures or something
  Column(
      modifier = Modifier.fillMaxHeight().padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Dice Name") },
            modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = numLayers,
            onValueChange = { numLayers = if (it.isDigitsOnly()) it else numLayers },
            label = { Text("Layers Count") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
              if (name.isEmpty() || numLayers.isEmpty()) return@Button
              viewModel.createNewDice(name, numLayers.toInt())
              onCreateNewDice()
            }) {
              Text("Next")
            }
      }
}

@Composable
fun SelectLayersScreen(
    viewModel: DiceCreationViewModel,
    imagesViewModel: DiceViewModel,
    onCreateNewDice: () -> Unit
) {
  // Display the list of templates
  val dice = viewModel.dice ?: return
  val size = viewModel.layersSize
  val images = imagesViewModel.imageMap
  val selection by remember { mutableStateOf(mutableMapOf<String, ImageModel>()) }

  // Display the list of images for the user to select
  Column(
      modifier = Modifier.fillMaxSize().padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2), modifier = Modifier.weight(1F)) {
              items(items = images.toList()) { (key, image) ->
                val isSelected = dice.layers.find { it.imageId == key } != null
                Column {
                  Box(
                      modifier =
                          Modifier.fillMaxSize().clickable {
                            if (selection[key] != null) {
                              selection.remove(key)
                            } else {
                              selection[key] = image
                            }
                            viewModel.updateSelectedImages(
                                selection.map { Layer(data = it.value, imageId = it.key) })
                          }) {
                        ImageBitmap(image = image, modifier = Modifier.padding(16.dp))
                        if (isSelected) {
                          Icon(
                              imageVector = Icons.Filled.Check,
                              contentDescription = "Checked",
                              modifier = Modifier)
                        }
                        if (isSelected)
                            Slider(
                                value =
                                    dice.layers.find { it.imageId == key }?.weight?.toFloat() ?: 1F,
                                onValueChange = {
                                  viewModel.changeWeight(weight = it.toInt(), imageId = key)
                                },
                                valueRange = 1f..size.toFloat(),
                                steps = size,
                                modifier =
                                    Modifier.fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .background(MaterialTheme.colorScheme.background),
                            )
                      }
                }
                // Display the image selection for each layer
              }
            }
        Button(
            modifier = Modifier.padding(16.dp),
            onClick = {
              // Update the selected images in the view model
              viewModel.updateSelectedImages(listOf())
              onCreateNewDice() // TODO name
            }) {
              Text("Next: (${dice.layers.sumOf {it.weight} ?:0} / $size)", fontSize = 24.sp)
            }
      }
}

@Composable
fun EditTemplateScreen(viewModel: DiceCreationViewModel, onSaveDice: () -> Unit) {
  val dice = viewModel.dice ?: return

  // Display the dice with the selected images and background color
  Box(modifier = Modifier.fillMaxSize().background(dice.backgroundColor)) {
    // Display the dice layers
  }

  //    ColorPicker(
  //        selectedColor = dice.backgroundColor,
  //        onColorSelected = { color ->
  //            viewModel.updateBackgroundColor(color)
  //        }
  //    )

  Button(
      onClick = {
        viewModel.saveDice()
        onSaveDice()
      }) {
        Text("Save Dice")
      }
}
