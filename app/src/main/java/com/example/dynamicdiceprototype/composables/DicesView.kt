package com.example.dynamicdiceprototype.composables

import OneScreenGrid
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.example.dynamicdiceprototype.composables.common.PopMenuWithAlert
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
    editDice: (Dice) -> Unit,
    modifier: Modifier = Modifier,
    states: List<Face> = listOf(), // TODO: append CurrentDices Class with States List
    viewModel: DiceViewModel? = null
) {
  OneScreenGrid<Dice>(dices, minSize = MAIN_SCREEN_DICE_MIN_SIZE, modifier) { dice, maxSize ->
    var showMenu by remember { mutableStateOf(false) }
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier.fillMaxSize()
                .combinedClickable(
                    onClick = {
                      if (states.isEmpty()) viewModel?.lockDice(dice)
                      else {
                        val nextIndex = states.selectNext(states.indexOf(dice.state))
                        viewModel?.setDiceState(dice, nextIndex?.let { states[nextIndex] })
                      }
                    },
                    onLongClick = { showMenu = true })) {
          DiceView(
              dice = dice,
              size = maxSize,
          )
          dice.state?.let {
            Box(modifier = Modifier.size(maxSize.div(5)).align(Alignment.TopStart)) {
              SizedImage(
                  image = it,
              )
            }
          }
          Box(Modifier.align(Alignment.Center)) {
            PopMenuWithAlert(
                actionItem = dice,
                items =
                    listOf(
                        MenuItem(
                            text = "Roll this die", callBack = { viewModel?.rollSingleDice(it) }),
                        MenuItem(
                            text =
                                (if (dice.diceLockState == DiceLockState.UNLOCKED) "Lock"
                                    else "Unlock")
                                    .plus(" this die"),
                            callBack = { viewModel?.lockDice(dice) }),
                        MenuItem(
                            text = "Duplicate this die",
                            callBack = { viewModel?.duplicateToCurrentDices(mapOf(it to 1)) }),
                        MenuItem(text = "Edit die", callBack = editDice),
                    ),
                showMenu = showMenu,
                onDismiss = { showMenu = false },
            )
          }
        }
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
            editDice = {},
            modifier = Modifier.weight(1f))

        Box(modifier = Modifier.fillMaxWidth().height(40.dp).background(Color.Cyan))
      }
    }
  }
}
