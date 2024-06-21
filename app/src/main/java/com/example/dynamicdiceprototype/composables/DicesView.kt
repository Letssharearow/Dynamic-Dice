package com.example.dynamicdiceprototype.composables

import OneScreenGrid
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dynamicdiceprototype.composables.common.PupMenuWithAlert
import com.example.dynamicdiceprototype.composables.createdice.DicePreview
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.DiceLockState
import com.example.dynamicdiceprototype.data.Face
import com.example.dynamicdiceprototype.data.MenuItem
import com.example.dynamicdiceprototype.services.DiceViewModel
import com.example.dynamicdiceprototype.services.getDices
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme
import com.example.dynamicdiceprototype.utils.MAIN_SCREEN_DICE_MIN_SIZE
import selectNext

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DicesView(
    dices: List<Dice>,
    modifier: Modifier = Modifier,
    states: List<Face> = listOf(), // TODO: append CurrentDices Class with States
    viewModel: DiceViewModel? = null
) {
  var showAddDiceDialog by remember { mutableStateOf(false) }
  OneScreenGrid<Dice>(dices, minSize = MAIN_SCREEN_DICE_MIN_SIZE, modifier) { dice, maxSize ->
    var showMenu by remember { mutableStateOf(false) }
    var currentStateIndex by remember { mutableStateOf<Int?>(null) }
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier.fillMaxSize()
                .combinedClickable(
                    onClick = {
                      if (states.isEmpty()) viewModel?.lockDice(dice)
                      else {
                        currentStateIndex = states.selectNext(currentStateIndex)
                      }
                    },
                    onLongClick = { showMenu = true })) {
          DiceView(
              dice = dice,
              size = maxSize,
          )
          currentStateIndex?.let {
            Box(modifier = Modifier.size(maxSize.div(5)).align(Alignment.TopStart)) {
              SizedImage(
                  image = states[it],
              )
            }
          }
          Box(Modifier.align(Alignment.Center)) {
            PupMenuWithAlert(
                actionItem = dice,
                items =
                    listOf(
                        MenuItem(
                            text = "Roll This Dice", callBack = { viewModel?.rollSingleDice(it) }),
                        MenuItem(
                            text =
                                (if (dice.diceLockState == DiceLockState.UNLOCKED) "Lock"
                                    else "Unlock")
                                    .plus(" this Dice"),
                            callBack = { viewModel?.lockDice(dice) }),
                        MenuItem(
                            text = "Duplicate this Dice",
                            callBack = { viewModel?.duplicateToCurrentDices(mapOf(it to 1)) }),
                        MenuItem(
                            text = "Add / Remove Dices", callBack = { showAddDiceDialog = true }),
                        MenuItem(
                            text = "Lock all Dices",
                            callBack = { viewModel?.setCurrentDicesState(DiceLockState.LOCKED) }),
                        MenuItem(
                            text = "Unlock all Dices",
                            callBack = { viewModel?.setCurrentDicesState(DiceLockState.UNLOCKED) }),
                        MenuItem(
                            text = "Reset all Dices",
                            callBack = { viewModel?.resetCurrentDices() }),
                    ),
                showMenu = showMenu,
                onDismiss = { showMenu = false },
            )
          }
        }
  }
  ItemSelectionDialog(
      showDialog = showAddDiceDialog,
      selectables = viewModel?.dices?.values?.toList() ?: listOf(),
      onDismiss = { showAddDiceDialog = false },
      onItemSelected = {
        viewModel?.setNewCurrentDices(it)
        showAddDiceDialog = false
      },
      initialValue = viewModel?.currentDices ?: listOf())
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
        val actualDice = selectables.find { it.id == multibleDice.id }!!
        newDices[actualDice] = newDices[actualDice]?.plus(1) ?: 1
      }
      mutableStateOf(newDices.toMap())
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Select an Item") },
        text = {
          SelectItemsGrid<Dice>(
              selectables = selectables,
              onSaveSelection = onItemSelected,
              getId = { it.name },
              initialValue = initialValueMapped,
              itemMinWidthPixel = 200f,
          ) { dice, modifier, maxWidth ->
            DicePreview(
                dice = dice, facesSum = dice.faces.sumOf { it.weight }, Modifier.size(maxWidth))
          }
        },
        confirmButton = { Button(onClick = onDismiss) { Text("Close") } },
        modifier = Modifier.fillMaxHeight(0.95f).fillMaxWidth(1f))
  }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
  DynamicDicePrototypeTheme {
    Box(Modifier.height(800.dp).width(300.dp)) {
      Column {
        Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(Color.Cyan))
        DicesView(
            dices = getDices(8),
            states = listOf(Face(contentDescription = "hi")),
            modifier = Modifier.weight(1f))

        Box(modifier = Modifier.fillMaxWidth().height(40.dp).background(Color.Cyan))
      }
    }
  }
}
