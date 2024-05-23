package com.example.dynamicdiceprototype.composables.wrapper

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.dynamicdiceprototype.Exceptions.DiceNotFoundException
import com.example.dynamicdiceprototype.LifecycleAwareComponent
import com.example.dynamicdiceprototype.composables.LandingPage
import com.example.dynamicdiceprototype.composables.createdice.diceGraph
import com.example.dynamicdiceprototype.composables.screens.DiceGroupCreationScreen
import com.example.dynamicdiceprototype.composables.screens.DiceGroupsScreen
import com.example.dynamicdiceprototype.composables.screens.TestScreen
import com.example.dynamicdiceprototype.composables.screens.UploadImageScreen
import com.example.dynamicdiceprototype.data.AlterBoxProperties
import com.example.dynamicdiceprototype.data.MenuItem
import com.example.dynamicdiceprototype.services.DiceViewModel
import com.example.dynamicdiceprototype.services.HeaderViewModel
import com.example.dynamicdiceprototype.services.PreferencesService
import com.example.dynamicdiceprototype.services.TAG

@Composable
fun NavGraph(navController: NavHostController) {
  val preferencesService: PreferencesService = PreferencesService
  val viewModel: DiceViewModel = viewModel<DiceViewModel>()
  val headerViewModel: HeaderViewModel = viewModel<HeaderViewModel>()
  val context = LocalContext.current
  viewModel.lastDiceGroup = preferencesService.loadLastBundle(context)
  if (viewModel.getErrorMessage() != null) {
    Toast.makeText(
            context,
            "Error Loading ${viewModel.getErrorMessage()}, check internet connection and restart app",
            Toast.LENGTH_LONG)
        .show()
  }
  if (viewModel.toastMessageText != null) {
    Toast.makeText(context, viewModel.toastMessageText, Toast.LENGTH_LONG).show()
  }

  LifecycleAwareComponent { viewModel.saveUser() }
  NavHost(navController, startDestination = Screen.MainScreen.route) {
    composable(route = Screen.TestScreen.route) { TestScreen() }
    composable(route = Screen.MainScreen.route) {
      remember {
        PreferencesService.saveLastBundle(context = context, viewModel.lastDiceGroup)
        headerViewModel.changeHeaderText(viewModel.lastDiceGroup)
        true
      }
      LandingPage(
          dices = viewModel.currentDices,
          name = viewModel.lastDiceGroup,
          isLoading = viewModel.collectFlows <= 1,
          onRollClicked = { viewModel.rollDices() })
    }
    diceGraph(viewModel, navController, headerViewModel)
    composable(route = Screen.UploadImage.route) {
      UploadImageScreen(context) { bitmap, name -> viewModel.uploadImage(bitmap, name) }
    }
    composable(route = Screen.DiceGroups.route) {
      DiceGroupsScreen(
          groups = viewModel.diceGroups.keys.toList(),
          onSelectGroup = { groupId ->
            try {
              viewModel.selectDiceGroup(groupId)
              navController.navigate(Screen.MainScreen.route)
              headerViewModel.changeHeaderText(groupId)
            } catch (e: NullPointerException) {
              Log.e(TAG, "One Dice is probably not found in the global dices ${e.message}")
            }
          },
          menuActions =
              listOf(
                  MenuItem(
                      text = "Edit dice group",
                      callBack = {
                        try {
                          viewModel.editGroup(it)
                          navController.navigate(Screen.CreateDiceGroup.route)
                          headerViewModel.changeHeaderText(it)
                        } catch (e: DiceNotFoundException) {
                          e.printStackTrace()
                          Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                        }
                      },
                  ),
                  MenuItem(
                      text = "Duplicate dice group",
                      callBack = { viewModel.duplicateGroup(it) },
                  ),
                  MenuItem(
                      text = "Delete dice group",
                      callBack = { viewModel.removeGroup(it) },
                      alert =
                          AlterBoxProperties(
                              "Cofirm Deletion",
                              "Pressing Confirm will Remove the Group entirely, there is no undoing")),
              ),
          onCreateNewGroup = {
            viewModel.createNewGroup()
            navController.navigate(Screen.CreateDiceGroup.route)
            headerViewModel.changeHeaderText("Create New Group")
          })
    }
    composable(route = Screen.CreateDiceGroup.route) {
      DiceGroupCreationScreen(
          dices = viewModel.dices.values.map { Pair(it, 0) },
          onCreateDiceGroup = { name, dices ->
            viewModel.createDiceGroup(name, dices)
            navController.navigate(Screen.DiceGroups.route)
          },
          groupSize = viewModel.groupSize,
          initialValue = viewModel.groupInEdit?.second,
          isEdit = viewModel.isGroupEditMode,
      )
    }
  }
}

private val createDiceRoute = "dice/create"

sealed class Screen(val route: String) {

  object MainScreen : Screen("home")

  object TestScreen : Screen("Test")

  object DiceGroups : Screen("dice_groups")

  object CreateDiceGroup : Screen("dice_groups/create")

  object UploadImage : Screen("upload")

  fun withArgs(vararg args: String): String {
    return buildString {
      append(route)
      args.forEach { append("/${it.replace(" ", "-").lowercase()}") }
    }
  }
}
