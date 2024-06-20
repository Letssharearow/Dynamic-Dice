package com.example.dynamicdiceprototype.composables

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.Face
import com.example.dynamicdiceprototype.services.DiceViewModel
import com.example.dynamicdiceprototype.services.TAG
import com.example.dynamicdiceprototype.services.getDices
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

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

@Composable
fun LandingPage(
    dices: List<Dice>,
    states: List<Face>,
    isLoading: Boolean,
    onRollClicked: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DiceViewModel? = null
) {
  Log.d(TAG, "Recompose LandingPage ${dices.size}")

  val lifecycleOwner = LocalLifecycleOwner.current
  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_STOP) {
        onClose()
      }
      if (event == Lifecycle.Event.ON_PAUSE) {
        onClose()
      }
      if (event == Lifecycle.Event.ON_DESTROY) {
        onClose()
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)

    // When the effect leaves the Composition, remove the observer
    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
  }

  // Your composable content goes here
  Column(
      verticalArrangement = Arrangement.SpaceBetween,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = modifier.fillMaxSize()) {
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.weight(1f)) {
          AnimatedVisibility(visible = !isLoading) {
            DiceBundle(dices = dices, states = states, viewModel = viewModel)
          }
          AnimatedVisibility(visible = isLoading) {
            CircularProgressIndicator(modifier = Modifier.wrapContentSize(align = Alignment.Center))
          }
        }
        DiceButtonM3(onRollClicked = onRollClicked, modifier = Modifier.padding(vertical = 16.dp))
      }
}

@Preview(showBackground = true)
@Composable
private fun prev() {
  DynamicDicePrototypeTheme {
    LandingPage(
        dices = getDices(5),
        isLoading = false,
        states = listOf(),
        onRollClicked = { /*TODO*/ },
        onClose = {})
  }
}
