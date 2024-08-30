package com.example.dynamicdiceprototype.composables

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.DiceState
import com.example.dynamicdiceprototype.data.Face
import com.example.dynamicdiceprototype.data.RollState
import com.example.dynamicdiceprototype.services.DiceViewModel
import com.example.dynamicdiceprototype.services.TAG
import com.example.dynamicdiceprototype.services.getDices
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Lock
import compose.icons.fontawesomeicons.solid.LockOpen

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
        var showHistory by remember { mutableStateOf(false) }

        Column(modifier = if (showHistory) modifier.weight(1f) else Modifier) {
          Row(
              horizontalArrangement = Arrangement.SpaceAround,
              modifier =
                  Modifier.fillMaxWidth()
                      .background(color = MaterialTheme.colorScheme.primaryContainer)
                      .padding(vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Sum: ${viewModel?.currentSum}",
                    style =
                        TextStyle(
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize))
                DiceButtonM3(
                    onRollClicked = onRollClicked, modifier = Modifier.padding(vertical = 16.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                      text = "Rolls: ${viewModel?.countRolls}",
                      style =
                          TextStyle(
                              color = Color.DarkGray,
                              fontWeight = FontWeight.Bold,
                              fontSize = MaterialTheme.typography.bodyLarge.fontSize))
                  Text(
                      text = "history",
                      style =
                          TextStyle(
                              color = Color.DarkGray, textDecoration = TextDecoration.Underline),
                      modifier =
                          Modifier.padding(top = 2.dp).clickable { showHistory = !showHistory })
                }
              }

          if (showHistory) {
            LazyColumn(modifier = Modifier.weight(9f)) {
              items(viewModel?.history ?: emptyList()) { rollState ->
                RollStateRow(rollState = rollState)
              }
            }
          }
        }
      }
}

@Composable
fun RollStateRow(rollState: RollState) {
  var expanded by remember { mutableStateOf(false) }

  Card(
      modifier = Modifier.fillMaxWidth(),
      border = BorderStroke(width = 1.dp, Color.Black),
      shape = RectangleShape,
      elevation =
          CardDefaults.cardElevation(
              defaultElevation = 4.dp) // Add elevation for better visual separation
      ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp) // Increase vertical padding
            ) {
              Row(
                  modifier =
                      Modifier.padding(start = 8.dp)) { // Add horizontal padding for "Roll" text
                    Text(text = "${rollState.rollId}")
                  }
              Text(text = "Sum: ${rollState.sum}")
              Text(text = "Dice: ${rollState.unlockedDicesCount}")
              IconButton(
                  onClick = { expanded = !expanded },
                  modifier = Modifier.padding(end = 8.dp) // Add padding for IconButton
                  ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand/Collapse",
                        tint = MaterialTheme.colorScheme.primary // Change icon color
                        )
                  }
            }

        if (expanded) {
          Column(
              modifier =
                  Modifier.padding(start = 16.dp)
                      .background(MaterialTheme.colorScheme.surfaceContainer)
                      .padding(start = 8.dp, end = 16.dp)) {
                // Detailed information about dice states
                rollState.diceStates.forEach { diceState ->
                  Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(2f)) {
                              if (diceState.locked)
                                  Icon(
                                      imageVector = FontAwesomeIcons.Solid.Lock,
                                      contentDescription = "lock",
                                      modifier = Modifier.size(16.dp),
                                  )
                              else
                                  Icon(
                                      imageVector = FontAwesomeIcons.Solid.LockOpen,
                                      contentDescription = "lock",
                                      modifier = Modifier.size(16.dp),
                                  )
                              Text(
                                  text = diceState.dice.plus(": ${diceState.faceName}"),
                                  modifier = Modifier.padding(start = 2.dp),
                                  overflow = TextOverflow.Ellipsis)
                            }
                        if (diceState.value != null)
                            Text(
                                text = "${diceState.value} (value)",
                                modifier = Modifier.padding(start = 8.dp).weight(1f),
                                overflow = TextOverflow.Ellipsis)
                        if (diceState.state != null)
                            Text(
                                text = "${diceState.state} (state)",
                                modifier = Modifier.padding(start = 8.dp).weight(1f),
                                overflow = TextOverflow.Ellipsis)
                      }
                }
              }
        }
      }
}

@Preview(showBackground = true)
@Composable
private fun Prev() {
  DynamicDicePrototypeTheme {
    LandingPage(
        dices = getDices(5),
        isLoading = false,
        states = listOf(),
        onRollClicked = { /*TODO*/ },
        onClose = {})
  }
}

@Preview
@Composable
private fun Preview() {
  DynamicDicePrototypeTheme {
    Column() {
      listOf(
              RollState(
                  1,
                  diceStates =
                      listOf(DiceState("hellohellohellohellohello", true, "hello", 1, "Frog"))))
          .forEach { RollStateRow(rollState = it) }
    }
  }
}
