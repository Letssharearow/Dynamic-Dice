package com.example.dynamicdiceprototype.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dynamicdiceprototype.Screen
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.Layer
import com.example.dynamicdiceprototype.services.DiceViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DiceCreationViewModel : ViewModel() {
  private val _dice = MutableStateFlow<Dice?>(null)
  val dice: StateFlow<Dice?> = _dice.asStateFlow()

  private val _templates = MutableStateFlow<List<Dice>>(emptyList())
  val templates: StateFlow<List<Dice>> = _templates.asStateFlow()

  fun createNewDice(name: String, numLayers: Int) {}

  fun updateSelectedImages(images: Array<Layer>) {
    _dice.value = _dice.value?.copy(layers = images)
  }

  fun updateBackgroundColor(color: Color) {
    _dice.value = _dice.value?.copy(backgroundColor = color)
  }

  fun saveDice() {
    // Save the dice to the templates list
    _templates.value = _templates.value + (_dice.value ?: return)
    _dice.value = null
  }
}

@Composable
fun CreateDiceNavGraph(imagesViewModel: DiceViewModel) {
  val viewModel: DiceCreationViewModel = viewModel<DiceCreationViewModel>()

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
    composable(route = Screen.SelectLayers.route) {
      EditTemplateScreen(viewModel) { navController.navigate(Screen.EditTemplate.route) }
    }
  }
}

@Composable
fun TemplateSelectionScreen(viewModel: DiceCreationViewModel, onCreateNewDice: () -> Unit) {
  // Display the list of templates
  LazyColumn(modifier = Modifier.fillMaxSize()) {
    items(viewModel.templates.value) { template ->
      // Display each template and handle selection
      Text(text = template.name)
    }
  }

  // Button to create a new dice
  Button(onClick = onCreateNewDice) { Text("Create New Dice") }
}

@Composable
fun TemplateCreationScreen(viewModel: DiceCreationViewModel, onCreateNewDice: () -> Unit) {

  var name by remember { mutableStateOf("") }
  var numLayers by remember { mutableStateOf(1) }
  // Display the list of templates
  Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(value = name, onValueChange = { name = it }, label = { Text("Dice Name") })

        Spacer(modifier = Modifier.height(16.dp))

        Slider(
            value = numLayers.toFloat(),
            onValueChange = { numLayers = it.toInt() },
            modifier = Modifier.fillMaxWidth(),
            valueRange = 1f..6f,
            steps = 5)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
              viewModel.createNewDice(name, numLayers)
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
  val dice = viewModel.dice.value ?: return
  val images = imagesViewModel.imageMap

  // Display the list of images for the user to select
  Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = {
              // Update the selected images in the view model
              viewModel.updateSelectedImages(arrayOf())
              onCreateNewDice() // TODO name
            }) {
              Text("Next")
            }
        //    LazyRow(modifier = Modifier.fillMaxSize()) {
        //      items(dice.layers.size) { index ->
        //        // Display the image selection for each layer
        //      }
        //    }
      }
}

@Composable
fun EditTemplateScreen(viewModel: DiceCreationViewModel, onSaveDice: () -> Unit) {
  val dice = viewModel.dice.value ?: return

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
