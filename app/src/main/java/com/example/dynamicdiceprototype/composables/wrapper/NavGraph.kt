package com.example.dynamicdiceprototype.composables.wrapper

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.dynamicdiceprototype.DTO.ImageDTO
import com.example.dynamicdiceprototype.Exceptions.DiceNotFoundException
import com.example.dynamicdiceprototype.composables.LandingPage
import com.example.dynamicdiceprototype.composables.common.AlertBox
import com.example.dynamicdiceprototype.composables.createdice.DicesScreen
import com.example.dynamicdiceprototype.composables.createdice.SelectFacesScreen
import com.example.dynamicdiceprototype.composables.createdice.diceGraph
import com.example.dynamicdiceprototype.composables.screens.DiceGroupCreationScreen
import com.example.dynamicdiceprototype.composables.screens.DiceGroupsScreen
import com.example.dynamicdiceprototype.composables.screens.ImagesActionsScreen
import com.example.dynamicdiceprototype.composables.screens.ProfileScreen
import com.example.dynamicdiceprototype.composables.screens.SaveImageScreen
import com.example.dynamicdiceprototype.composables.screens.SettingsScreen
import com.example.dynamicdiceprototype.composables.screens.TestScreen
import com.example.dynamicdiceprototype.data.AlterBoxProperties
import com.example.dynamicdiceprototype.data.Face
import com.example.dynamicdiceprototype.data.MenuItem
import com.example.dynamicdiceprototype.services.DiceViewModel
import com.example.dynamicdiceprototype.services.FirebaseDataStore
import com.example.dynamicdiceprototype.services.HeaderViewModel
import com.example.dynamicdiceprototype.services.TAG
import com.example.dynamicdiceprototype.utils.temp_group_id

@Composable
fun NavGraph(navController: NavHostController, viewModel: DiceViewModel) {
  val headerViewModel: HeaderViewModel = viewModel<HeaderViewModel>()
  val context = LocalContext.current
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

  NavHost(navController, startDestination = Screen.MainScreen.route) {
    composable(route = Screen.TestScreen.route) { TestScreen() }
    composable(route = Screen.Settings.route) { SettingsScreen() }
    composable(route = Screen.Profile.route) {
      ProfileScreen { navController.navigate(Screen.Settings.route) }
    }
    composable(route = Screen.MainScreen.route) {
      LaunchedEffect(viewModel.dices, viewModel.diceGroups) {
        viewModel.selectDiceGroup(temp_group_id)
      }
      LandingPage(
          dices = viewModel.currentDices,
          isLoading = !viewModel.hasLoadedUser,
          states =
              viewModel.diceGroups[temp_group_id]?.states?.map { imageKey ->
                val image = viewModel.imageMap[imageKey]
                image?.let {
                  Face(
                      contentDescription = it.contentDescription,
                      data = FirebaseDataStore.base64ToBitmap(it.base64String))
                } ?: Face(contentDescription = imageKey)
              } ?: listOf(),
          onRollClicked = { viewModel.rollDices() },
          viewModel = viewModel,
          onClose = { viewModel.saveCurrentDices() })
    }
    diceGraph(viewModel, navController, headerViewModel)
    composable(route = Screen.SaveImage.route) {
      SaveImageScreen(
          context = context,
          onImagesSelected = { images ->
            viewModel.saveImages(images)
            Toast.makeText(context, "Images Saved", Toast.LENGTH_SHORT).show()
          },
          onNavigateToDiceCreation = {
            navController.navigate(DicesScreen.SelectFaces.route)
            viewModel.createNewDice()
          })
    }
    composable(route = Screen.Images.route) {
      SelectFacesScreen(
          faces =
              viewModel.imageMap.values.filter {
                it.contentDescription != "image"
              }, // TODO this filters images that were null or threw an error on firebase, maybe a
          // better handling for that, because "image" seems to be hardcoded
          color = Color.Transparent,
          initialValue = emptyMap(),
          onFacesSelectionClick = { // TODO Consider using same datatype (map probably) for
            // everything)
            viewModel.changeSelectedImages(it)
            navController.navigate(Screen.ImagesActions.route)
            headerViewModel.changeHeaderText("Image actions")
          })
    }
    composable(route = Screen.ImagesActions.route) {
      ImagesActionsScreen(
          images = viewModel.selectedImages,
          onCreateDice = {},
          onDeleteImages = {
            viewModel.deleteImages(it)
            navController.navigate(Screen.Images.route)
          })
    }
    composable(route = Screen.DiceGroups.route) {
      LaunchedEffect(true) { headerViewModel.changeHeaderText("Dice Groups") }
      DiceGroupsScreen(
          groups = viewModel.diceGroups.values.toList(),
          onSelectGroup = { group ->
            try {
              viewModel.saveDiceGroup(group)
              navController.navigate(Screen.MainScreen.route)
              headerViewModel.changeHeaderText(group.name)
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
                          viewModel.setGroupInEdit(it.id)
                          navController.navigate(Screen.CreateDiceGroup.route)
                          headerViewModel.changeHeaderText(it.name)
                        } catch (e: DiceNotFoundException) {
                          e.printStackTrace()
                          Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                        }
                      },
                  ),
                  MenuItem(
                      text = "Duplicate dice group",
                      callBack = { viewModel.duplicateGroup(it.id) },
                  ),
                  MenuItem(
                      text = "Delete dice group",
                      callBack = { viewModel.removeGroup(it.id) },
                      alert =
                          AlterBoxProperties(
                              "Cofirm Deletion",
                              "Pressing Confirm will Remove the Group entirely, there is no undoing")),
              ),
          onCreateNewGroup = {
            viewModel.createNewGroup()
            navController.navigate(Screen.CreateDiceGroup.route)
          })
    }
    composable(route = Screen.CreateDiceGroup.route) {
      var openDialog by remember { mutableStateOf(false) }
      LaunchedEffect(true) { headerViewModel.changeHeaderText("Create New Group") }
      DiceGroupCreationScreen(
          dices = viewModel.dices.values.toList(),
          initialValue = viewModel.groupInEdit,
          onSaveSelection = { name, dices ->
            viewModel.setGroupInEditDices(name, dices)
            if (!viewModel.isGroupEditMode &&
                viewModel.diceGroups
                    .filter { it.value.name.equals(name, ignoreCase = true) }
                    .isNotEmpty()) {
              openDialog = true
            } else {
              navController.navigate(Screen.CreateDiceGroupStates.route)
            }
          },
      )
      AlertBox(
          isOpen = openDialog,
          text = "Name Already Exists, are you sure you want to overwrite it?",
          onDismiss = { openDialog = false },
          conConfirm = {
            viewModel.saveGroupInEdit()
            openDialog = false
            navController.navigate(Screen.CreateDiceGroupStates.route)
          })
    }
    composable(route = Screen.CreateDiceGroupStates.route) {
      LaunchedEffect(true) { headerViewModel.changeHeaderText("Select optional Cycling State") }

      SelectFacesScreen(
          faces =
              viewModel.imageMap.values.filter {
                it.contentDescription != "image"
              }, // TODO this filters images that were null or threw an error on firebase, maybe
          // a
          // better handling for that, because "image" seems to be hardcoded
          color = Color.Transparent,
          initialValue =
              viewModel.groupInEdit
                  ?.states
                  ?.associateBy({ viewModel.imageMap[it] ?: ImageDTO() }, { 1 }) ?: mapOf(),
          onFacesSelectionClick = {
            viewModel.setSelectedStates(it)
            viewModel.saveGroupInEdit()
            navController.navigate(Screen.DiceGroups.route)
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

  object SaveImage : Screen("save")

  object Profile : Screen("profile")

  object Settings : Screen("settings")

  object Images : Screen("images")

  object ImagesActions : Screen("images/action")

  fun withArgs(vararg args: String): String {
    return buildString {
      append(route)
      args.forEach { append("/${it.replace(" ", "-").lowercase()}") }
    }
  }
}
