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
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dynamicdiceprototype.composables.common.PupMenuWithAlert
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.MenuItem
import com.example.dynamicdiceprototype.services.DiceViewModel
import com.example.dynamicdiceprototype.services.getDices
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme
import com.example.dynamicdiceprototype.utils.MAIN_SCREEN_DICE_MIN_SIZE

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DicesView(dices: List<Dice>, modifier: Modifier = Modifier) {
  val inPreviewMode = LocalInspectionMode.current
  val viewModel: DiceViewModel? = if (inPreviewMode) null else viewModel()
  OneScreenGrid<Dice>(dices, minSize = MAIN_SCREEN_DICE_MIN_SIZE, modifier) { dice, maxSize ->
    var showMenu by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier.fillMaxSize()
                .combinedClickable(
                    onClick = { viewModel?.lockDice(dice) }, onLongClick = { showMenu = true })) {
          DiceView(
              dice = dice,
              size = maxSize,
          )
          Box(Modifier.align(Alignment.Center)) {
            PupMenuWithAlert(
                actionItem = dice,
                items =
                    listOf(
                        //                      MenuItem(text = "Roll", callBack =
                        // {viewModel.rollSingleDice(it)}),
                        //                      MenuItem(text = "Lock", callBack =
                        // {viewModel.lockSingleDice(it)}),
                        MenuItem(
                            text = "Duplicate", callBack = { viewModel?.duplicateCurrentDice(it) }),
                        //                      MenuItem(text = "Add Dice", callBack =
                        // {viewModel.addDiceToCurrentDices(it)}),
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
        DicesView(dices = getDices(8), Modifier.weight(1f))

        Box(modifier = Modifier.fillMaxWidth().height(40.dp).background(Color.Cyan))
      }
    }
  }
}
