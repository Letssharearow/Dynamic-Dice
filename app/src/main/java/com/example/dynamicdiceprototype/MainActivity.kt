package com.example.dynamicdiceprototype

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dynamicdiceprototype.composables.ImageBitmap
import com.example.dynamicdiceprototype.composables.LandingPage
import com.example.dynamicdiceprototype.services.DiceViewModel
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      DynamicDicePrototypeTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          //          Column { images.values.forEach { ImageFromBase64(imageBitmap = it) } }
          MyApp()
        }
      }
    }
  }
}

@Composable
fun DiceCreationView() {
  val viewModel: DiceViewModel = viewModel<DiceViewModel>()
  val map = viewModel.imageMap
  Column(Modifier.verticalScroll(state = ScrollState(0))) {
    map.values.map { ImageBitmap(imageBitmap = it) }
  }
}

@Composable
fun MyApp() {

  val navController = rememberNavController()
  val viewModel: DiceViewModel = viewModel<DiceViewModel>()
  val name = "Julis Dice Bundle"

  NavHost(navController, startDestination = Screen.CreateDice.route) {
    composable(route = Screen.MainScreen.route) {}
    composable(route = "home") {
      LandingPage(
          dices = viewModel.dicesState,
          name = name,
      ) // TODO refactor this
    }
    composable(route = Screen.CreateDice.route) { DiceCreationView() }
  }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  DynamicDicePrototypeTheme { DiceCreationView() }
}
