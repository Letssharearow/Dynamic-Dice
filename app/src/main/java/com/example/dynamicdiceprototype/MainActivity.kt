package com.example.dynamicdiceprototype

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.dataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.dynamicdiceprototype.DTO.UserDTO
import com.example.dynamicdiceprototype.composables.wrapper.Menu
import com.example.dynamicdiceprototype.composables.wrapper.Screen
import com.example.dynamicdiceprototype.services.DiceSerializer
import com.example.dynamicdiceprototype.services.HeaderViewModel
import com.example.dynamicdiceprototype.services.PreferenceManager
import com.example.dynamicdiceprototype.services.TestSerializer
import com.example.dynamicdiceprototype.services.TestStorableObject
import com.example.dynamicdiceprototype.services.UserConfigSerializer
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme
import kotlinx.coroutines.launch

val Context.dataStore by dataStore("dices-settings.json", DiceSerializer)
val Context.userDataStore by dataStore("user-settings.json", UserConfigSerializer)
val Context.imagesDataStore by dataStore("images-settings.json", UserConfigSerializer)
val Context.testDataStore by dataStore("test-settings.json", TestSerializer)

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val res = resources
    //    uploadUser()
    //    uploadDices()
    //    uploadImages(res)
    //    val firebase = FirebaseDataStore()

    PreferenceManager.init(this)
    setContent {
      DynamicDicePrototypeTheme {
        val testSettings = testDataStore.data.collectAsState(initial = TestStorableObject())
        val scope = rememberCoroutineScope()
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          MyApp()
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column {
              Text(text = testSettings.value.text)
              Button(onClick = { scope.launch { testDataStore("new text") } }) {
                Text(text = "PRESS ME TO CHANGE TEXT")
              }
            }
          }
        }
      }
    }
  }

  private suspend fun setUser(user: UserDTO) {
    userDataStore.updateData { t -> user }
  }

  private suspend fun testDataStore(text: String) {
    testDataStore.updateData { t -> t.copy(text = text) }
  }
}

@Composable
fun MyApp() {
  val scope = rememberCoroutineScope()
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val navController = rememberNavController()
  Column {
    AppBar({ scope.launch { drawerState.apply { if (isClosed) open() else close() } } }) {
      navController.navigate(Screen.Profile.route)
    }
    Menu(drawerState = drawerState, scope = scope, navController = navController)
  }
}

@Composable
fun LifecycleAwareComponent(onClose: () -> Unit) {
  val lifecycleOwner = LocalLifecycleOwner.current

  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_STOP) {
        // App is being closed, save data here
        onClose()
      }
    }

    // Add the observer to the lifecycle
    lifecycleOwner.lifecycle.addObserver(observer)

    // When the effect leaves the Composition, remove the observer
    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
  }

  // Your composable content goes here
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
