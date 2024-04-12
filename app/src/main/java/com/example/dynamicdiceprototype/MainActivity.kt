package com.example.dynamicdiceprototype

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun MyApp() {
  var dices by remember { mutableStateOf(getDices()) }
  val name = "Julis Cooli Bundle"
  LandingPage(
      dices = dices,
      name = name,
      onRollClicked = { dices = dices.map { dice -> dice.copy(current = dice.roll()) } })
}

@Composable
fun DiceView(dice: Dice, onDiceClick: () -> Unit, modifier: Modifier = Modifier) {

  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    // Use the state variable to display the current value of the dice
    Button(
        onClick = onDiceClick,
        shape = RoundedCornerShape(4.dp), // Explicitly set the shape here
        modifier = Modifier.padding(8.dp)) { // Smaller value for less rounded corners
          Text(
              text = "${dice.current} ${dice.state}",
              fontSize = 24.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(8.dp))
        }
  }
}

@Composable
fun DicesView(dices: List<Dice>, modifier: Modifier = Modifier) {
  LazyVerticalGrid(columns = GridCells.Adaptive(100.dp)) {
    items(dices) { dice ->
      // Remember the state of each dice
      var diceState by remember { mutableStateOf(dice) }

      DiceView(
          dice = diceState, onDiceClick = { diceState = diceState.copy(state = DiceState.LOCKED) })
    }
  }
}

@Composable
fun Bundle(dices: List<Dice>, name: String, modifier: Modifier = Modifier) {
  // Create a state variable to hold the current value of the dice
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(text = name, fontSize = 36.sp)
    DicesView(dices)
  }
}

@Composable
fun LandingPage(
    dices: List<Dice>,
    name: String,
    onRollClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Bundle(dices, name)
    OutlinedButton(onClick = onRollClicked) { Text(text = "roll") }
  }
}

fun getDices(): List<Dice> {
  return listOf(
      Dice(listOf(Layer("1"), Layer("2"), Layer("3"), Layer("4"), Layer("5"), Layer("6"))),
      Dice(listOf(Layer("1"), Layer("2"), Layer("3"), Layer("4"), Layer("5"), Layer("6"))),
      Dice(listOf(Layer("1"), Layer("2"), Layer("3"), Layer("4"), Layer("5"), Layer("6"))),
      Dice(listOf(Layer("1"), Layer("2"), Layer("3"), Layer("4"), Layer("5"), Layer("6"))),
      Dice(listOf(Layer("1"), Layer("2"), Layer("3"), Layer("4"), Layer("5"), Layer("6"))),
  )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  DynamicDicePrototypeTheme { MyApp() }
}
