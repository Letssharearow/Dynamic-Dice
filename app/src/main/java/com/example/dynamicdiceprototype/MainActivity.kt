package com.example.dynamicdiceprototype

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val dices =
                listOf(
                    Dice(
                        listOf(
                            Layer("1"),
                            Layer("2"),
                            Layer("3"),
                            Layer("4"),
                            Layer("5"),
                            Layer("6")
                        )
                    ),
                    Dice(
                        listOf(
                            Layer("1"),
                            Layer("2"),
                            Layer("3"),
                            Layer("4"),
                            Layer("5"),
                            Layer("6")
                        )
                    ),
                    Dice(
                        listOf(
                            Layer("1"),
                            Layer("2"),
                            Layer("3"),
                            Layer("4"),
                            Layer("5"),
                            Layer("6")
                        )
                    ),
                    Dice(
                        listOf(
                            Layer("1"),
                            Layer("2"),
                            Layer("3"),
                            Layer("4"),
                            Layer("5"),
                            Layer("6")
                        )
                    ),
                    Dice(
                        listOf(
                            Layer("1"),
                            Layer("2"),
                            Layer("3"),
                            Layer("4"),
                            Layer("5"),
                            Layer("6")
                        )
                    ),
                )
            DynamicDicePrototypeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DicesView(dices)
                }
            }
        }
    }
}

@Composable
fun DiceView(dice: Dice, modifier: Modifier = Modifier) {
    // Create a state variable to hold the current value of the dice
    var diceValue by remember { mutableStateOf(dice.roll()) }

    Box(
        modifier = modifier
    ) {
        Button(
            onClick = {
                // Roll the dice and update the state variable
                diceValue = dice.roll()
            },
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            // Use the state variable to display the current value of the dice
            Text(text = "$diceValue", modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun DicesView(dices: List<Dice>, modifier: Modifier = Modifier) {
    // Create a state variable to hold the current value of the dice
    Column {
        Text(text = "Bundle Name")
        LazyVerticalGrid(columns = GridCells.Adaptive(100.dp)) {
            items(dices) {
                DiceView(dice = it)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val dices =
        listOf(
            Dice(listOf(Layer("1"), Layer("2"), Layer("3"), Layer("4"), Layer("5"), Layer("6"))),
            Dice(listOf(Layer("1"), Layer("2"), Layer("3"), Layer("4"), Layer("5"), Layer("6"))),
            Dice(listOf(Layer("1"), Layer("2"), Layer("3"), Layer("4"), Layer("5"), Layer("6"))),
            Dice(listOf(Layer("1"), Layer("2"), Layer("3"), Layer("4"), Layer("5"), Layer("6"))),
            Dice(listOf(Layer("1"), Layer("2"), Layer("3"), Layer("4"), Layer("5"), Layer("6"))),
        )

    DynamicDicePrototypeTheme {
        DicesView(dices)
    }
}