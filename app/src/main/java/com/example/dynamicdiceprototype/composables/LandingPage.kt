package com.example.dynamicdiceprototype.composables

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.dynamicdiceprototype.composables.common.PopMenuWithAlert
import com.example.dynamicdiceprototype.composables.createdice.DicePreview
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.DiceLockState
import com.example.dynamicdiceprototype.data.DiceState
import com.example.dynamicdiceprototype.data.Face
import com.example.dynamicdiceprototype.data.MenuItem
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
        var showAddDiceDialog by remember { mutableStateOf(false) }
        var showMenu by remember { mutableStateOf(false) }
        Column(modifier = if (showHistory) modifier.weight(1f) else Modifier) {
          Row(
              horizontalArrangement = Arrangement.SpaceAround,
              modifier =
                  Modifier.fillMaxWidth()
                      .background(color = MaterialTheme.colorScheme.primaryContainer)
                      .padding(vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                      text = "Sum: ${viewModel?.currentSum}",
                      style =
                          TextStyle(
                              color = Color.DarkGray,
                              fontWeight = FontWeight.Bold,
                              fontSize = MaterialTheme.typography.bodyLarge.fontSize))
                  Text(
                      text = if (showHistory) "hide history" else "show history",
                      style =
                          TextStyle(
                              color = Color.DarkGray, textDecoration = TextDecoration.Underline),
                      modifier = Modifier.padding(top = 2.dp).clickable { showMenu = true })
                }

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
                      text = if (showHistory) "hide history" else "show history",
                      style =
                          TextStyle(
                              color = Color.DarkGray, textDecoration = TextDecoration.Underline),
                      modifier =
                          Modifier.padding(top = 2.dp).clickable { showHistory = !showHistory })
                }
              }
          PopMenuWithAlert(
              actionItem = Dice(),
              items =
                  listOf(
                      MenuItem(text = "Add / Remove dice", callBack = { showAddDiceDialog = true }),
                      MenuItem(
                          text = "Lock all dice",
                          callBack = { viewModel?.setCurrentDicesState(DiceLockState.LOCKED) }),
                      MenuItem(
                          text = "Unlock all dice",
                          callBack = { viewModel?.setCurrentDicesState(DiceLockState.UNLOCKED) }),
                      MenuItem(text = "Reset", callBack = { viewModel?.resetRollingScreen() }),
                  ),
              showMenu = showMenu,
              onDismiss = { showMenu = false },
          )
          ItemSelectionDialog(
              showDialog = showAddDiceDialog,
              selectables = viewModel?.dices?.values?.toList() ?: listOf(),
              onDismiss = { showAddDiceDialog = false },
              onItemSelected = {
                viewModel?.setNewCurrentDices(it)
                showAddDiceDialog = false
              },
              initialValue = viewModel?.currentDices ?: listOf())

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
fun ItemSelectionDialog(
    showDialog: Boolean,
    selectables: List<Dice>,
    onDismiss: () -> Unit,
    initialValue: List<Dice>,
    onItemSelected: (Map<Dice, Int>) -> Unit
) {
  if (showDialog) {
    val initialValueMapped by remember {
      val newDices = mutableMapOf<Dice, Int>()
      initialValue.forEach { multibleDice ->
        val actualDice = selectables.find { it.id == multibleDice.id }
        if (actualDice != null) newDices[actualDice] = newDices[actualDice]?.plus(1) ?: 1
      }
      mutableStateOf(newDices.toMap())
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add or Remove dice") },
        text = {
          SelectItemsGrid<Dice>(
              selectables = selectables,
              onSaveSelection = onItemSelected,
              getId = { it.name },
              itemMinWidthPixel = 400f,
              initialValue = initialValueMapped,
              view = { dice, modifier, maxWidth, _ ->
                DicePreview(
                    dice = dice, facesSum = dice.faces.sumOf { it.weight }, Modifier.size(maxWidth))
              },
              minValue = 1,
          )
        },
        properties = DialogProperties(usePlatformDefaultWidth = false),
        confirmButton = { Button(onClick = onDismiss) { Text("Cancel") } },
        modifier = Modifier.fillMaxHeight(0.95f).fillMaxWidth(0.98f))
  }
}

@Composable
fun RollStateRow(rollState: RollState) {
  var expanded by remember { mutableStateOf(false) }
  val isEmptyState = rollState.rollId != -1

  Card(
      modifier = Modifier.fillMaxWidth(),
      border = BorderStroke(width = 1.dp, Color.Black),
      shape = RectangleShape,
      elevation =
          CardDefaults.cardElevation(
              defaultElevation = 4.dp)
      ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier.fillMaxWidth()
            ) {
              if (isEmptyState) {
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
              } else {
                Box(
                    modifier =
                        Modifier.fillMaxWidth()
                            .height(8.dp)
                            .background(MaterialTheme.colorScheme.tertiary))
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
