package com.example.dynamicdiceprototype

import android.content.res.Resources
import android.graphics.BitmapFactory
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
import com.example.dynamicdiceprototype.services.FirebaseDataStore
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val res = resources
    uploadRessources(res)

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

fun uploadRessources(res: Resources) {
  val firbase = FirebaseDataStore()
  data class ImageModelSetDTO(val image: Int, val name: String)
  val ids =
      arrayOf<ImageModelSetDTO>(
          ImageModelSetDTO(image = R.drawable.rukaiya_rectangular, name = "rukaiya_rectangular"),
          ImageModelSetDTO(image = R.drawable.two_transparent, name = "two_transparent"),
          ImageModelSetDTO(image = R.drawable.one_transparent, name = "one_transparent"),
          ImageModelSetDTO(image = R.drawable.three_transparent, name = "three_transparent"),
          ImageModelSetDTO(image = R.drawable.four_transparent, name = "four_transparent"),
          ImageModelSetDTO(image = R.drawable.five_transparent, name = "five_transparent"),
          ImageModelSetDTO(image = R.drawable.six_transparent, name = "six_transparent"))
  ids.forEach {
    var bitmap = BitmapFactory.decodeResource(res, it.image)
    firbase.uploadBitmap(
        "${it.image}",
        name = it.name,
        bitmap = bitmap) // TODO find out why changing the "name" to something else than Id breaks
  }
}

@Composable
fun DiceCreationView() {
  val viewModel: DiceViewModel = viewModel<DiceViewModel>()
  val map = viewModel.imageMap
  Column(Modifier.verticalScroll(state = ScrollState(0))) {
    map.values.map {
      ImageBitmap(
          image = it,
      )
    }
  }
}

@Composable
fun MyApp() {

  val navController = rememberNavController()
  val viewModel: DiceViewModel = viewModel<DiceViewModel>()
  val name = "Julis Dice Bundle"

  NavHost(navController, startDestination = Screen.MainScreen.route) {
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
