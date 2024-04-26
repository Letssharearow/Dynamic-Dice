package com.example.dynamicdiceprototype.composables

import OneScreenGrid
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dynamicdiceprototype.Screen
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.Layer
import com.example.dynamicdiceprototype.services.DiceCreationViewModel
import com.example.dynamicdiceprototype.services.DiceViewModel

@Composable
fun CreateDiceNavGraph(imagesViewModel: DiceViewModel) {
  val viewModel = DiceCreationViewModel(imagesViewModel.configuration.dices)

  val navController = rememberNavController()

  NavHost(navController, startDestination = Screen.Templates.route) {
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
      EditTemplateScreen(viewModel) {
        navController.navigate(Screen.Templates.route)
        viewModel.saveDice()
      }
    }
  }
}

@Composable
fun DiceCard(dice: Dice) {
  Surface(
      modifier = Modifier.padding(16.dp), shadowElevation = 8.dp, color = dice.backgroundColor) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
          Text(text = dice.name, style = MaterialTheme.typography.displayLarge, color = Color.Black)
          Spacer(modifier = Modifier.height(8.dp))
          Text(text = "Layers (${dice.layers.size}):", style = MaterialTheme.typography.bodyLarge)
          LazyVerticalGrid(
              columns = GridCells.Adaptive(20.dp),
              modifier = Modifier.heightIn(max = 40.dp).padding(top = 4.dp) // Set a max height
              ) {
                items(dice.layers) { layer ->
                  Box(Modifier.padding(end = 3.dp)) { LayerView(layer, 20.dp) }
                }
              }
        }
      }
}

@Composable
fun TemplateSelectionScreen(viewModel: DiceCreationViewModel, onCreateNewDice: () -> Unit) {

  ArrangedColumn {
    LazyColumn() {
      items(viewModel.templates) { template ->
        // Display each template and handle selection
        Box(modifier = Modifier.padding(top = 16.dp).clickable { onCreateNewDice() }) {
          DiceCard(template)
        }
      }
    }
    ContinueButton(onClick = onCreateNewDice, text = "Create New Dice")
  }
}

@Composable
fun TemplateCreationScreen(viewModel: DiceCreationViewModel, onCreateName: () -> Unit) {

  var name by remember { mutableStateOf("Change Later") }
  var numLayers by remember { mutableStateOf("") }
  // Display the list of templates
  // TODO Add some cool pictures or something
  ArrangedColumn {
    Column(Modifier.padding(16.dp)) {
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
    }
    ContinueButton(
        onClick = {
          if (name.isNotEmpty() && numLayers.isNotEmpty()) {
            viewModel.createNewDice(name, numLayers.toInt())
            onCreateName()
          }
        },
        text = "Next")
  }
}

@Composable
fun SelectLayersScreen(
    viewModel: DiceCreationViewModel,
    imagesViewModel: DiceViewModel,
    onLayersSelectionClick: () -> Unit
) {
  // Display the list of templates
  var layers = remember { mutableStateMapOf<String, Layer>() }

  val size = viewModel.layersSize
  val images = imagesViewModel.imageMap

  // Display the list of images for the user to select
  ArrangedColumn {
    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.weight(1F)) {
      items(items = images.toList()) { (key, image) ->
        val matchingLayer = layers[key]
        val imageIsSelected = matchingLayer != null
        val weight = matchingLayer?.weight?.toFloat() ?: 1F
        BoxWithConstraints(
            modifier =
                Modifier.clickable {
                  val layer = layers[key]
                  if (layer == null) layers[key] = Layer(imageId = key, data = image)
                  else layers.remove((key))
                }) {
              val width = constraints.maxWidth
              val density = LocalDensity.current
              val maxWidthDp = with(density) { width.toDp() }

              if (imageIsSelected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Checked",
                    modifier = Modifier.align(Alignment.TopEnd))
              }
              ImageBitmap(image = image, modifier = Modifier.size(maxWidthDp).padding(16.dp))
              if (imageIsSelected) {
                Slider(
                    value = weight,
                    onValueChange = { value ->
                      val layer = layers[key]
                      if (layer != null) layers[key] = layer.copy(weight = value.toInt())
                    },
                    valueRange = 1f..size.toFloat(),
                    steps = size,
                    modifier =
                        Modifier.align(Alignment.BottomCenter)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer))
              }
            }
        // Display the image selection for each layer
      }
    }
    ContinueButton(
        onClick = {
          viewModel.updateSelectedLayers(layers)
          onLayersSelectionClick()
        },
        text = "Next: (${layers.values.sumOf {it.weight} ?:0} / $size)")
  }
}

@Composable
fun EditTemplateScreen(viewModel: DiceCreationViewModel, onSaveDice: () -> Unit) {
  val dice = viewModel.dice
  val layers = dice.layers

  ArrangedColumn {
    OneScreenGrid(items = layers, minSize = 10F, modifier = Modifier.weight(1F)) { item, maxWidth ->
      LayerView(layer = item, size = maxWidth)
    }
    ContinueButton(onClick = onSaveDice, text = "Save Dice")
  }
}

@Composable
fun ContinueButton(onClick: () -> Unit, text: String) {
  Button(modifier = Modifier.padding(16.dp), onClick = onClick) { Text(text, fontSize = 24.sp) }
}

@Composable
fun ArrangedColumn(content: @Composable ColumnScope.() -> Unit) {
  Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.SpaceBetween,
      horizontalAlignment = Alignment.CenterHorizontally) {
        content()
      }
}
