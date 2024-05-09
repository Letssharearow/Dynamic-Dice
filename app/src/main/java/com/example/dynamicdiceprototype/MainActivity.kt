package com.example.dynamicdiceprototype

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dynamicdiceprototype.composables.ImageBitmap
import com.example.dynamicdiceprototype.composables.Menu
import com.example.dynamicdiceprototype.services.DiceViewModel
import com.example.dynamicdiceprototype.services.FirebaseDataStore
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val res = resources
    //    uploadRessources(res)

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

fun uploadRessources(res: Resources) {
  val firbase = FirebaseDataStore()
  data class ImageModelSetDTO(val image: Int, val name: String)
  val images =
      arrayOf<ImageModelSetDTO>(
          ImageModelSetDTO(image = R.drawable.cameleon, name = "cameleon"),
          ImageModelSetDTO(image = R.drawable.elephant, name = "elephant"),
          ImageModelSetDTO(image = R.drawable.fish, name = "fish"),
          ImageModelSetDTO(image = R.drawable.frog, name = "frog"),
          ImageModelSetDTO(image = R.drawable.lion, name = "lion"),
          ImageModelSetDTO(image = R.drawable.monkey, name = "monkey"),
          ImageModelSetDTO(image = R.drawable.owl, name = "owl"),
          ImageModelSetDTO(image = R.drawable.parrot, name = "parrot"),
          ImageModelSetDTO(image = R.drawable.penguin, name = "penguin"),
      )
  images.forEach {
    var bitmap = BitmapFactory.decodeResource(res, it.image)
    firbase.uploadBitmap("${it.image}", name = it.name, bitmap = bitmap)
  }
}

fun uploadColors(context: Context) {
  val firebase = FirebaseDataStore()
  val imageCreator = ImageCreator()

  // Assuming you have a list of colors
  val colors =
      listOf(
          Pair(Color.Red, "red"),
          Pair(Color(0xFFFF7F00), "orange"), // Orange
          Pair(Color.Yellow, "yellow"),
          Pair(Color.Green, "green"),
          Pair(Color.Blue, "blue"),
          Pair(Color(0xFF4B0082), "indigo"), // Indigo
          Pair(Color(0xFF8B00FF), "violet"), // Violet
          // Additional colors
          Pair(Color(0xFFFFC0CB), "pink"), // Pink
          Pair(Color(0xFFFFD700), "gold"), // Gold
          Pair(Color.Cyan, "cyan"),
          Pair(Color(0xFFFFA500), "light orange"), // Light Orange
          Pair(Color(0xFF800080), "purple"), // Purple
          Pair(Color.Magenta, "magenta"),
          Pair(Color(0xFFC0C0C0), "silver"), // Silver
          Pair(Color.Gray, "gray"),
          Pair(Color.Black, "black"),
          Pair(Color.White, "white"))

  for (color in colors) {
    val namedColor = color.second
    val bitmap = imageCreator.getBitmap(400, color.first.toArgb())
    firebase.uploadBitmap("${color.first.toArgb()}", name = namedColor, bitmap = bitmap)
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
  //  val context = LocalContext.current
  //  uploadColors(context)

  val scope = rememberCoroutineScope()
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  Column {
    AppBar({
      scope.launch { drawerState.apply { if (isClosed) open() else close() } }
    }) { /* TODO Handle profile picture button click */}
    Menu(drawerState = drawerState, scope = scope)
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(onMenuClicked: () -> Unit, onProfileClicked: () -> Unit) {
  TopAppBar(
      title = { Text(text = "Header Text") },
      navigationIcon = {
        IconButton(onClick = { onMenuClicked() }) {
          Icon(Icons.Filled.Menu, contentDescription = "Menu")
        }
      },
      actions = {
        IconButton(onClick = onProfileClicked) {
          Icon(Icons.Filled.AccountCircle, contentDescription = "Profile")
        }
      })
}

@Preview(showBackground = true)
@Composable
fun AppBarPreview() {
  DynamicDicePrototypeTheme {
    AppBar({ /* Handle navigation icon click */}) { /* Handle profile picture button click */}
  }
}
