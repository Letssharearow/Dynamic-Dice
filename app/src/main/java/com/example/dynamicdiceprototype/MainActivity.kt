package com.example.dynamicdiceprototype

import OneScreenGrid
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.dataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.dynamicdiceprototype.composables.screens.OnboardingScreen
import com.example.dynamicdiceprototype.composables.wrapper.Menu
import com.example.dynamicdiceprototype.services.DiceViewModel
import com.example.dynamicdiceprototype.services.DiceViewModelFactory
import com.example.dynamicdiceprototype.services.HeaderViewModel
import com.example.dynamicdiceprototype.services.PreferenceKey
import com.example.dynamicdiceprototype.services.PreferenceManager
import com.example.dynamicdiceprototype.services.Screen
import com.example.dynamicdiceprototype.services.serializer.DiceSerializer
import com.example.dynamicdiceprototype.services.serializer.ImageSerializer
import com.example.dynamicdiceprototype.services.serializer.UserConfigSerializer
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme
import compose.icons.AllIcons
import compose.icons.FontAwesomeIcons
import kotlinx.coroutines.launch

val Context.diceDataStore by dataStore("dices-settings.json", DiceSerializer)
val Context.userDataStore by dataStore("user-settings.json", UserConfigSerializer)
val Context.imagesDataStore by dataStore("images-settings.json", ImageSerializer)

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    PreferenceManager.init(this)
    setContent {
      DynamicDicePrototypeTheme {
        // A surface container using the 'background' color from the theme
        val viewModel: DiceViewModel by viewModels {
          DiceViewModelFactory(imagesDataStore, diceDataStore, userDataStore, resources)
        }
        val hasOnboardingCompleted =
            PreferenceManager.getPreferenceFlow<Boolean>(PreferenceKey.HasOnboardingCompleted)
                .collectAsState(initial = true) // TODO set to false after creating onboarding
                .value
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          if (hasOnboardingCompleted) {
            MyApp(viewModel)
          } else {
            OnboardingScreen()
          }
        }
      }
    }
  }
}

@Composable
fun Icons(modifier: Modifier = Modifier) {
  OneScreenGrid(items = FontAwesomeIcons.AllIcons, minSize = 200f) { item, maxSize ->
    Button(
        border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.secondaryContainer),
        onClick = {}) {
          Column {
            Icon(imageVector = item, contentDescription = item.name)
            Text(text = item.name)
          }
        }
  }
}

@Composable
fun MyApp(viewModel: DiceViewModel) {
  val scope = rememberCoroutineScope()
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val navController = rememberNavController()
  Column {
    AppBar({ scope.launch { drawerState.apply { if (isClosed) open() else close() } } }) {
      navController.navigate(Screen.Profile.route)
    }
    Menu(
        drawerState = drawerState,
        scope = scope,
        navController = navController,
        viewModel = viewModel)
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(onMenuClicked: () -> Unit, onProfileClicked: () -> Unit) {
  val viewModel = viewModel<HeaderViewModel>()
  TopAppBar(
      title = { Text(text = viewModel.headerText) },
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
    AppBar({ /* Handle navigation icon click */ }) { /* Handle profile picture button click */ }
  }
}
