package com.example.dynamicdiceprototype.composables.wrapper

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.dynamicdiceprototype.LifecycleAwareComponent
import com.example.dynamicdiceprototype.Screen
import com.example.dynamicdiceprototype.composables.LandingPage
import com.example.dynamicdiceprototype.composables.createdice.CreateDiceNavGraph
import com.example.dynamicdiceprototype.composables.screens.DiceGroupCreationScreen
import com.example.dynamicdiceprototype.composables.screens.DiceGroupsScreen
import com.example.dynamicdiceprototype.composables.screens.TestScreen
import com.example.dynamicdiceprototype.composables.screens.UploadImageScreen
import com.example.dynamicdiceprototype.services.DiceViewModel
import com.example.dynamicdiceprototype.services.TAG

@Composable
fun NavGraph(navController: NavHostController) {
  val viewModel: DiceViewModel = viewModel<DiceViewModel>()
  val context = LocalContext.current

  LifecycleAwareComponent { viewModel.saveUser() }
  NavHost(navController, startDestination = Screen.MainScreen.route) {
    composable(route = Screen.TestScreen.route) { TestScreen() }
    composable(route = Screen.MainScreen.route) {
      LandingPage(
          dices = viewModel.currentDices,
          name = viewModel.lastBundle,
      )
    }
    composable(route = Screen.CreateDice.route) { CreateDiceNavGraph(viewModel) }
    composable(route = Screen.UploadImage.route) {
      UploadImageScreen(context, { bitmap, name -> viewModel.uploadImage(bitmap, name) })
    }
    composable(route = Screen.DiceGroups.route) {
      DiceGroupsScreen(
          viewModel.bundles.keys.toList(),
          { groupId ->
            try {
              viewModel.selectDiceGroup(groupId)
              navController.navigate(Screen.MainScreen.route)
            } catch (e: NullPointerException) {
              Log.e(TAG, "One Dice is probably not found in the global dices ${e.message}")
            }
          },
          {})
    }
    composable(route = Screen.CreateDiceGroup.route) {
      DiceGroupCreationScreen(
          dices = viewModel.dices.values.map { Pair(it, 1) },
          onCreateDiceGroup = { name, dices ->
            viewModel.createDiceGroup(name, dices)
            navController.navigate(Screen.DiceGroups.route)
          },
          groupSize = 4,
      )
    }
  }
}