package com.example.dynamicdiceprototype

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val dice =
                Dice(listOf(Layer("1"), Layer("2"), Layer("3"), Layer("4"), Layer("5"), Layer("6")))
            DynamicDicePrototypeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DiceView(dice)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun DiceView(dice: Dice, modifier: Modifier = Modifier) {
    // Create a state variable to hold the current value of the dice
    var diceValue by remember { mutableStateOf(dice.roll()) }

    Button(
        onClick = {
            // Roll the dice and update the state variable
            diceValue = dice.roll()
        }
    ) {
        // Use the state variable to display the current value of the dice
        Text(text = "$diceValue")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DynamicDicePrototypeTheme {
    }
}