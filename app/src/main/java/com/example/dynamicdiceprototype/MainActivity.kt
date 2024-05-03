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
  val scope = rememberCoroutineScope()
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  Column {
    AppBar({
      scope.launch { drawerState.apply { if (isClosed) open() else close() } }
    }) { /* Handle profile picture button click */}
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
