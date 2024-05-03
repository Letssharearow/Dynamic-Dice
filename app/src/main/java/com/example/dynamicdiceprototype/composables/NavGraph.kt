package com.example.dynamicdiceprototype.composables

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.dynamicdiceprototype.Screen
import com.example.dynamicdiceprototype.composables.createdice.CreateDiceNavGraph
import com.example.dynamicdiceprototype.services.DiceViewModel

@Composable
fun NavGraph(navController: NavHostController) {
  val viewModel: DiceViewModel = viewModel<DiceViewModel>()
  val name = "Julis Dice Bundle"
  NavHost(navController, startDestination = Screen.CreateDice.route) {
    composable(route = Screen.MainScreen.route) {
      LandingPage(
          dices = viewModel.dicesState,
          name = name,
      ) // TODO refactor this
    }
    composable(route = Screen.CreateDice.route) { CreateDiceNavGraph(viewModel) }
    composable(route = Screen.DiceGroups.route) { DiceGroupsScreen(viewModel, {}, {}) }
  }
}
