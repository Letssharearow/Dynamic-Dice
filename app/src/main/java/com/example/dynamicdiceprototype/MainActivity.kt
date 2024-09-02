package com.example.dynamicdiceprototype

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.datastore.dataStore
import androidx.navigation.compose.rememberNavController
import com.example.dynamicdiceprototype.composables.AppBar
import com.example.dynamicdiceprototype.composables.screens.OnboardingScreen
import com.example.dynamicdiceprototype.composables.wrapper.Menu
import com.example.dynamicdiceprototype.services.PreferenceKey
import com.example.dynamicdiceprototype.services.PreferenceManager
import com.example.dynamicdiceprototype.services.Screen
import com.example.dynamicdiceprototype.services.serializer.DiceSerializer
import com.example.dynamicdiceprototype.services.serializer.ImageSerializer
import com.example.dynamicdiceprototype.services.serializer.UserConfigSerializer
import com.example.dynamicdiceprototype.services.view_model.DiceViewModel
import com.example.dynamicdiceprototype.services.view_model.DiceViewModelFactory
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme
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
