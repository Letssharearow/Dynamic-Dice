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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.draw.shadow
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
fun DiceView(dice: Dice, size: Float, modifier: Modifier = Modifier) {
  val viewModel: DiceViewModel = viewModel<DiceViewModel>()

  Log.i("MyApp", "Recompose DiceView dices $dice")
  Box(contentAlignment = Alignment.Center, modifier = modifier.size(size = size.dp)) {
    Button(
        onClick = { viewModel.lockDice(dice) },
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(0.dp),
        modifier =
            Modifier.aspectRatio(1F)
                .graphicsLayer {
                  rotationZ = dice.rotation
                  val scale = 1 / 1.4F
                  scaleX = scale
                  scaleY = scale
                }
                .shadow(8.dp, RoundedCornerShape(20.dp))) {
          Image(
              painter =
                  painterResource(
                      id = dice.current?.imageId ?: 0), // TODO make current not null or something
              contentDescription = dice.current?.data ?: "No Dice",
              modifier = Modifier.fillMaxSize().padding(16.dp))
        }
    if (dice.state == DiceState.LOCKED) {
      Icon(
          imageVector = Icons.Filled.Lock,
          contentDescription = "LOCKED",
          modifier = Modifier.align(Alignment.TopEnd).size(36.dp))
    }
  }
}

// TODO create utils class, add tests
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

  LazyVerticalGrid(columns = GridCells.Adaptive(minSize = maxWidth.dp)) {
    items(dices) { dice -> DiceView(dice = dice, size = maxWidth) }
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
  Column() {
    Bundle(dices, name)
    DiceButtonM3(
        onRollClicked = { viewModel.rollDices() },
        modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 16.dp))
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
