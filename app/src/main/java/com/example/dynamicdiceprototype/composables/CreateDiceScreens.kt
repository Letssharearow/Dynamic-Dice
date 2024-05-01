package com.example.dynamicdiceprototype.composables

import OneScreenGrid
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.semantics.Role.Companion.Switch
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dynamicdiceprototype.Screen
import com.example.dynamicdiceprototype.data.Configuration.Dice
import com.example.dynamicdiceprototype.data.Face
import com.example.dynamicdiceprototype.services.DiceViewModel
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

@Composable
fun CreateDiceNavGraph(diceViewModel: DiceViewModel) {
  val navController = rememberNavController()

  NavHost(navController, startDestination = Screen.Templates.route) {
    composable(route = Screen.Templates.route) {
      TemplateSelectionScreen(
          diceViewModel, onSelectTemplate = { navController.navigate(Screen.EditTemplate.route) }) {
            diceViewModel.setFaceSize(it)
            navController.navigate(Screen.SelectFaces.route)
          }
    }
    composable(route = Screen.SelectFaces.route) {
      SelectFacesScreen(diceViewModel) { navController.navigate(Screen.EditTemplate.route) }
    }
    composable(route = Screen.EditTemplate.route) {
      EditTemplateScreen(
          diceViewModel,
          onEdit = { navController.navigate(Screen.SelectFaces.route) },
          onSaveDice = {
              navController.navigate(Screen.Templates.route)
              diceViewModel.saveDice()
          })
    }
  }
}

@Composable
fun DiceCard(dice: Dice, isCompact: Boolean) {
  val facesSum = dice.faces.sumOf { it.weight }

  Surface(
      shadowElevation = 8.dp,
      color = dice.backgroundColor,
      modifier =
          Modifier.padding(8.dp)
              .border(
                  BorderStroke(2.dp, MaterialTheme.colorScheme.secondary),
                  RoundedCornerShape(16.dp))
              .clip(RoundedCornerShape(16.dp))) {
        if (isCompact) {
          CompactDiceCard(dice.name, facesSum)
        } else
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp).background(dice.backgroundColor)) {
                  Text(
                      text = dice.name,
                      style = MaterialTheme.typography.displayMedium,
                      color = Color.Black,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                      modifier = Modifier.fillMaxWidth(0.66F).padding(16.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Box(
                      Modifier.fillMaxWidth()
                          .aspectRatio(1f)
                          .border(BorderStroke(2.dp, Color.Gray), RoundedCornerShape(16.dp))
                          .clip(RoundedCornerShape(16.dp))) {
                        OneScreenGrid(
                            items = dice.faces,
                            minSize = 10f,
                        ) { face, maxWidthDp ->
                          FaceView(
                              face,
                              maxWidthDp,
                              showWeight = false,
                              modifier =
                                  Modifier.border(
                                          BorderStroke(1.dp, Color.Gray), RoundedCornerShape(4.dp))
                                      .clip(RoundedCornerShape(4.dp)))
                        }
                        if(facesSum > 10){
                            Box(
                                modifier =
                                Modifier.wrapContentSize()
                                    .aspectRatio(1f)
                                    .padding(20.dp) // TODO better size adjustment, wrapContentSize
                                    // didnt work
                                    .background(Color(0x80000000), CircleShape)
                                    .border(BorderStroke(2.dp, Color.White), CircleShape)) {
                                Text(
                                    text = "$facesSum",
                                    style = MaterialTheme.typography.displayMedium,
                                    color = Color.White,
                                    modifier = Modifier.align(Alignment.Center).wrapContentSize())
                            }
                        }
                      }
                }
      }
}

@Composable
private fun CompactDiceCard(name: String, facesSum: Int) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceAround,
      modifier = Modifier.fillMaxWidth()) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleSmall,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(0.66F))

        NumberCircle(facesSum.toString())
      }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  DynamicDicePrototypeTheme {
    CompactDiceCard(
        name =
            "Change LaterChange LaterChange LaterChange LaterChange LaterChange LaterChange LaterChange LaterChange LaterChange LaterChange LaterChange LaterChange LaterChange LaterChange LaterChange LaterChange LaterChange LaterChange LaterChange LaterChange LaterChange Later",
        facesSum = 20)
  }
}

@Composable
fun TemplateSelectionScreen(
    viewModel: DiceViewModel,
    onSelectTemplate: () -> Unit,
    onCreateNewDice: (number: Int) -> Unit
) {

  var isCompact by remember { mutableStateOf(false) }

  Column {
    // Toggle switch for isCompact
    Row(
        modifier = Modifier.fillMaxWidth().padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
          Text(
              "Copy or Create New Dice",
          )
          Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.End) {
                Text("Toggle View")
                Switch(checked = isCompact, onCheckedChange = { isCompact = it })
              }
        }

    ArrangedColumn {
      LazyColumn() {
        items(viewModel.getDices().values.toList()) { template ->
          // Display each template and handle selection
          Box(
              modifier =
                  Modifier.clickable {
                    viewModel.setStartDice(template)
                    onSelectTemplate()
                  }) {
                DiceCard(template, isCompact)
              }
        }
      }
      Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.wrapContentSize().padding(vertical = 16.dp)) {
            // Input field for numbers
            var number by remember { mutableStateOf<String?>("6") }
            OutlinedTextField(
                value = number.toString(),
                onValueChange = { newValue -> number = newValue.takeIf { it.isNotEmpty() } ?: "" },
                label = { Text("New Dice Faces Count") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.wrapContentSize(),
                isError = number.isNullOrEmpty())
            // Continue button
            ContinueButton(
                onClick = { onCreateNewDice(number?.toInt() ?: 0) }, // TODO Better handling?
                text = "+",
                enabled = !number.isNullOrEmpty())
          }
    }
  }
}

@Composable
fun StringInput(viewModel: DiceViewModel, onCreateName: () -> Unit) {

  var name by remember { mutableStateOf("Change Later") }
  var numFaces by remember { mutableStateOf("") }
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
          value = numFaces,
          onValueChange = { numFaces = if (it.isDigitsOnly()) it else numFaces },
          label = { Text("Faces Count") },
          keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
          modifier = Modifier.fillMaxWidth())
    }
    ContinueButton(
        onClick = {
          if (name.isNotEmpty() && numFaces.isNotEmpty()) {
            viewModel.createNewDice(name, numFaces.toInt())
            onCreateName()
          }
        },
        text = "Next")
  }
}

@Composable
fun SelectFacesScreen(viewModel: DiceViewModel, onFacesSelectionClick: () -> Unit) {
  val faces = remember {
    mutableStateMapOf(*viewModel.dice.faces.map { Pair(it.imageId, it) }.toTypedArray())
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
            FaceView(face = faces[key]?: Face(data = image), size = maxWidthDp)
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
                            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5F)))
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

@Composable
fun EditTemplateScreen(viewModel: DiceViewModel, onSaveDice: () -> Unit, onEdit: () -> Unit) {
  var name by remember { mutableStateOf(viewModel.dice.name) }
  ArrangedColumn {
    //color picker
    Input(
        text = name,
        onValueChange = { name = it + ((Math.random() * 26) + 48).toInt().toChar() },
        label = "Dice Name",
        Modifier.padding(8.dp))
    OneScreenGrid(items = viewModel.dice.faces, minSize = 10F, modifier = Modifier.weight(1F)) {
        item,
        maxWidth ->
      FaceView(face = item, size = maxWidth, showWeight = true)
    }
    Row(horizontalArrangement = Arrangement.SpaceAround) {
      ContinueButton(
          onClick = {
            viewModel.setDiceName(name)
            onEdit()
          },
          text = "Edit")
      ContinueButton(
          onClick = {
            viewModel.setDiceName(name)
            onSaveDice()
          },
          text = "Save Dice")
    }
  }
}

@Composable
private fun Input(
    text: String,
    onValueChange: (text: String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
  OutlinedTextField(
      value = text,
      onValueChange = onValueChange,
      label = { Text(label) },
      singleLine = true,
      modifier = modifier.fillMaxWidth(),
  )
}

@Composable
fun ContinueButton(onClick: () -> Unit, text: String, enabled: Boolean = true) {
  Button(modifier = Modifier.padding(16.dp), onClick = onClick, enabled = enabled) {
    Text(text, fontSize = 24.sp)
  }
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
