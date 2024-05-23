package com.example.dynamicdiceprototype

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dynamicdiceprototype.composables.wrapper.Menu
import com.example.dynamicdiceprototype.services.HeaderViewModel
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme
import com.example.dynamicdiceprototype.utils.uploadImages
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val res = resources
    //    uploadUser()
    //    uploadDices()
    uploadImages(res)
    //    val firebase = FirebaseDataStore()

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
  val scope = rememberCoroutineScope()
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  Column {
    AppBar({
      scope.launch { drawerState.apply { if (isClosed) open() else close() } }
    }) { /* TODO Handle profile picture button click */
    }
    Menu(drawerState = drawerState, scope = scope)
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
