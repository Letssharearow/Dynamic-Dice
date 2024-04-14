package com.example.dynamicdiceprototype

import DiceButtonM3
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme
import kotlin.math.max
import kotlin.math.min

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      DynamicDicePrototypeTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          MyApp()
        }
      }
    }
  }
}

@Composable
fun DiceView(dice: Dice, modifier: Modifier = Modifier) {
  val viewModel: DiceViewModel = viewModel<DiceViewModel>()

  Log.i("MyApp", "Recompose DiceView dices $dice")
  Box(contentAlignment = Alignment.Center, modifier = modifier) {
    Button(
        onClick = { viewModel.lockDice(dice) },
        shape = RoundedCornerShape(16.dp),
        modifier =
            Modifier.fillMaxWidth().fillMaxHeight().graphicsLayer {
              rotationZ = dice.rotation
              val scale = 1 / 1.4F
              scaleX = scale
              scaleY = scale
            }) {
          Image(
              painter = painterResource(id = dice.current?.imageId ?: 0),
              contentDescription = dice.current?.data ?: "No Dice",
              modifier = Modifier.padding(0.dp))
        }
    if (dice.state == DiceState.LOCKED) {
      Icon(
          imageVector = Icons.Filled.Lock,
          contentDescription = "LOCKED",
          modifier = Modifier.align(Alignment.TopEnd).size(36.dp))
    }
  }
}

fun getMaxWidth(count: Int, width: Int, height: Int): Float {
  var currentMaxWidth = 0F
  for (i in 1..count) {
    currentMaxWidth = max(currentMaxWidth, min((height.toFloat() * i) / count, width.toFloat() / i))
  }
  return currentMaxWidth
}

fun main() {
  println(getMaxWidth(1, 411, 814))
  println(getMaxWidth(2, 411, 814))
  println(getMaxWidth(4, 411, 814))
  println(getMaxWidth(9, 411, 814))
  println(getMaxWidth(16, 411, 814))
}

@Composable
fun DicesView(dices: List<Dice>, modifier: Modifier = Modifier) {
  val screenWidth = LocalConfiguration.current.screenWidthDp
  val screenHeight = LocalConfiguration.current.screenHeightDp

  val maxWidth = getMaxWidth(dices.size, width = screenWidth, height = screenHeight)

  Log.i("MyApp", "screenWidth $screenWidth")
  Log.i("MyApp", "screenHeight $screenHeight")

  LazyVerticalGrid(columns = GridCells.Adaptive(maxWidth.dp)) {
    items(dices) { dice -> DiceView(dice = dice, modifier = Modifier.size(maxWidth.dp)) }
  }
}

@Composable
fun Bundle(dices: List<Dice>, name: String, modifier: Modifier = Modifier) {
  // Create a state variable to hold the current value of the dice
  Log.i("MyApp", "Recompose Bundle dices $dices")
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(text = name, fontSize = 36.sp)
    DicesView(dices)
  }
}

@Composable
fun LandingPage(dices: List<Dice>, name: String, modifier: Modifier = Modifier) {
  val viewModel: DiceViewModel = viewModel<DiceViewModel>()

  Log.i("MyApp", "Recompose Landing Page dices $dices")
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Bundle(dices, name)
    DiceButtonM3(onRollClicked = { viewModel.rollDices() })
  }
}

@Composable
fun MyApp() {
  val viewModel: DiceViewModel = viewModel<DiceViewModel>()
  val name = "Julis Dice Bundle"
  LandingPage(
      dices = viewModel.dicesState,
      name = name,
  )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  DynamicDicePrototypeTheme { MyApp() }
}
