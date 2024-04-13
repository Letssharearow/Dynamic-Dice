package com.example.dynamicdiceprototype

import DiceButtonM3
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

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
  Box(contentAlignment = Alignment.Center) {
    Button(
        onClick = { viewModel.lockDice(dice) },
        shape = RoundedCornerShape(4.dp),
        modifier =
            Modifier.padding(8.dp).graphicsLayer {
              rotationZ = dice.rotation
              val scale = 1 / 1.4F
              scaleX = scale.toFloat()
              scaleY = scale.toFloat()
            }) {
          Text(
              text = "${dice.current}",
              fontSize = 24.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(8.dp))
        }
    // Overlay the lock icon if the dice is LOCKED
    if (dice.state == DiceState.LOCKED) {
      Icon(
          imageVector = Icons.Filled.Lock,
          contentDescription = "LOCKED",
          modifier = Modifier.align(Alignment.TopEnd).size(24.dp).padding(4.dp))
    }
  }
}

@Composable
fun DicesView(dices: List<Dice>, modifier: Modifier = Modifier) {
  LazyVerticalGrid(columns = GridCells.Adaptive(100.dp)) {
    items(dices) { dice -> DiceView(dice = dice) }
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
  val name = "Julis Cooli Bundle"
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
