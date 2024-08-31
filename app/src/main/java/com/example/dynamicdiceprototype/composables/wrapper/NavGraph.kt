package com.example.dynamicdiceprototype.composables.wrapper

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.dynamicdiceprototype.DTO.ImageDTO
import com.example.dynamicdiceprototype.Exceptions.DiceNotFoundException
import com.example.dynamicdiceprototype.composables.LandingPage
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
import com.example.dynamicdiceprototype.services.DicesScreen
import com.example.dynamicdiceprototype.services.FirebaseDataStore
import com.example.dynamicdiceprototype.services.HeaderViewModel
import com.example.dynamicdiceprototype.services.Screen
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
      LaunchedEffect(true) { headerViewModel.changeHeaderText("Profile") }
      ProfileScreen { navController.navigate(Screen.Settings.route) }
    }
    composable(route = Screen.MainScreen.route) {
      LaunchedEffect(true) {
        headerViewModel.changeHeaderText(viewModel.currentDiceGroupName ?: "Roll Dice")
      }
      val states by remember {
        derivedStateOf {
          viewModel.diceGroups[temp_group_id]?.states?.map { imageKey ->
            val image = viewModel.imageMap[imageKey]
            image?.let {
              Face(
                  contentDescription = it.contentDescription,
                  data = FirebaseDataStore.base64ToBitmap(it.base64String))
            } ?: Face(contentDescription = imageKey)
          } ?: listOf()
        }
      }

      LaunchedEffect(viewModel.dices, viewModel.diceGroups) {
        if (viewModel.currentDices.isEmpty()) {
          viewModel.selectDiceGroup(temp_group_id)
        }
      }
      LandingPage(
          dices = viewModel.currentDices,
          isLoading = viewModel.currentDices.isEmpty(),
          states = states,
          onRollClicked = { viewModel.rollDices() },
          viewModel = viewModel,
          onEditDice = {
            viewModel.editRollingDie(it)
            navController.navigate(DicesScreen.EditDice.route)
          },
          onClose = { viewModel.saveCurrentDices() })
    }
    diceGraph(viewModel, navController, headerViewModel)
    composable(route = Screen.SaveImage.route) {
      LaunchedEffect(true) { headerViewModel.changeHeaderText("Add Images") }
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
      LaunchedEffect(true) { headerViewModel.changeHeaderText("Images") }
      SelectFacesScreen(
          faces =
              viewModel.imageMap.values.filter {
                it.contentDescription != "image"
              }, // TODO this filters images that were null in datastore "image" is the default
          // value
          color = Color.Transparent,
          initialValue = emptyMap(),
          addNumber = false,
          minValue = 1,
          showSlider = false,
          onFacesSelectionClick = {
            viewModel.changeSelectedImages(it)
            navController.navigate(Screen.ImagesActions.route)
          })
    }
    composable(route = Screen.ImagesActions.route) {
      LaunchedEffect(true) { headerViewModel.changeHeaderText("Images Actions") }
      ImagesActionsScreen(
          images = viewModel.selectedImages,
          onCreateDice = {
            viewModel.createNewDice()
            viewModel.setSelectedFaces(it)
            navController.navigate(DicesScreen.EditDice.route)
          },
          onDeleteImages = {
            viewModel.deleteImages(it)
            navController.navigate(Screen.SaveImage.route)
            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
          })
    }
    composable(route = Screen.DiceGroups.route) {
      LaunchedEffect(true) { headerViewModel.changeHeaderText("Dice Groups") }
      DiceGroupsScreen(
          groups = viewModel.diceGroups.values.toList(),
          onSelectGroup = { group ->
            try {
              viewModel.selectDiceGroup(group)
              navController.navigate(Screen.MainScreen.route)
            } catch (e: NullPointerException) {
              Log.e(TAG, "One die is probably not found in the global dice ${e.message}")
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
          onCreateRandomGroup = {
            viewModel.setTempGroupRandom()
            navController.navigate(Screen.MainScreen.route)
          },
          onCreateNewGroup = {
            viewModel.createNewGroup()
            navController.navigate(Screen.CreateDiceGroup.route)
          })
    }
    composable(route = Screen.CreateDiceGroup.route) {
      LaunchedEffect(true) { headerViewModel.changeHeaderText("Create Dice Group") }
      DiceGroupCreationScreen(
          dices = viewModel.dices.values.toList(),
          initialValue = viewModel.groupInEdit,
          onSaveSelection = { name, dices ->
            viewModel.setGroupInEditDices(name, dices)
            if (!viewModel.isGroupEditMode &&
                viewModel.diceGroups
                    .filter { it.value.name.equals(name, ignoreCase = true) }
                    .isNotEmpty()) {
              viewModel.saveGroupInEdit()
              navController.navigate(Screen.CreateDiceGroupStates.route)
            } else {
              navController.navigate(Screen.CreateDiceGroupStates.route)
            }
          },
      )
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
          addNumber = false,
          showSlider = false,
          minValue = 1,
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
