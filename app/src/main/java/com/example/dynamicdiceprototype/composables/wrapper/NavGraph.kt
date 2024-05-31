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
import com.example.dynamicdiceprototype.DTO.ImageDTO
import com.example.dynamicdiceprototype.Exceptions.DiceNotFoundException
import com.example.dynamicdiceprototype.LifecycleAwareComponent
import com.example.dynamicdiceprototype.composables.LandingPage
import com.example.dynamicdiceprototype.composables.createdice.SelectFacesScreen
import com.example.dynamicdiceprototype.composables.createdice.diceGraph
import com.example.dynamicdiceprototype.composables.screens.DiceGroupCreationScreen
import com.example.dynamicdiceprototype.composables.screens.DiceGroupsScreen
import com.example.dynamicdiceprototype.composables.screens.TestScreen
import com.example.dynamicdiceprototype.composables.screens.UploadImageScreen
import com.example.dynamicdiceprototype.data.AlterBoxProperties
import com.example.dynamicdiceprototype.data.MenuItem
import com.example.dynamicdiceprototype.services.DiceViewModel
import com.example.dynamicdiceprototype.services.HeaderViewModel
import com.example.dynamicdiceprototype.services.PreferenceKey
import com.example.dynamicdiceprototype.services.PreferenceManager
import com.example.dynamicdiceprototype.services.TAG

@Composable
fun NavGraph(navController: NavHostController) {
  val viewModel: DiceViewModel = viewModel<DiceViewModel>()
  val headerViewModel: HeaderViewModel = viewModel<HeaderViewModel>()
  val context = LocalContext.current
  val savedGroup = PreferenceManager.loadData<String>(PreferenceKey.LastDiceGroup)
  viewModel.lastDiceGroup = savedGroup

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
        PreferenceManager.saveData(PreferenceKey.LastDiceGroup, viewModel.lastDiceGroup)
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
      UploadImageScreen(context) { images ->
        viewModel.uploadImages(images)
        Toast.makeText(context, "Upload Successful", Toast.LENGTH_SHORT).show()
      }
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
                          viewModel.setGroupInEdit(it)
                          navController.navigate(Screen.CreateDiceGroup.route)
                          headerViewModel.changeHeaderText(it)
                        } catch (e: DiceNotFoundException) {
                          e.printStackTrace()
                          Toast.makeText(context, e.message, Toast.LENGTH_LONG)
                              .show() // TODO actually wait for Firebase success
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
          dices = viewModel.dices.values.toList(),
          onSaveSelection = { name, dices ->
            viewModel.setGroupInEditDices(name, dices)
            navController.navigate(Screen.CreateDiceGroupStates.route)
          },
          groupSize = viewModel.groupSize,
          isEdit = viewModel.isGroupEditMode,
      )
    }
    composable(route = Screen.CreateDiceGroupStates.route) {
      if (viewModel.imageMap.isEmpty()) {
        viewModel.loadAllImages()
      }
      SelectFacesScreen(
          faces =
              viewModel.imageMap.values.filter {
                it.contentDescription != "image"
              }, // TODO this filters images that were null or threw an error on firebase, maybe a
          // better handling for that
          color = viewModel.diceInEdit.backgroundColor,
          initialValue =
              viewModel.groupInEdit
                  ?.second
                  ?.states
                  ?.associateBy({ viewModel.imageMap[it] ?: ImageDTO() }, { 1 }) ?: mapOf(),
          onFacesSelectionClick = {
            viewModel.setSelectedStates(it)
            viewModel.saveGroupInEdit()
            navController.navigate(Screen.DiceGroups.route)
            headerViewModel.changeHeaderText("Create New Group")
          })
    }
  }
}

sealed class Screen(val route: String) {

  object MainScreen : Screen("home")

  object TestScreen : Screen("Test")

  object DiceGroups : Screen("dice_groups")

  object CreateDiceGroup : Screen("dice_groups/create")

  object CreateDiceGroupStates : Screen("dice_groups/create/states")

  object UploadImage : Screen("upload")

  fun withArgs(vararg args: String): String {
    return buildString {
      append(route)
      args.forEach { append("/${it.replace(" ", "-").lowercase()}") }
    }
  }
}
