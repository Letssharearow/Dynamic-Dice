package com.example.dynamicdiceprototype.composables.createdice

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.dynamicdiceprototype.DTO.ImageDTO
import com.example.dynamicdiceprototype.Exceptions.PermittedActionException
import com.example.dynamicdiceprototype.data.AlterBoxProperties
import com.example.dynamicdiceprototype.data.MenuItem
import com.example.dynamicdiceprototype.services.DiceViewModel
import com.example.dynamicdiceprototype.services.DicesScreen
import com.example.dynamicdiceprototype.services.DieEditMode
import com.example.dynamicdiceprototype.services.HeaderViewModel
import com.example.dynamicdiceprototype.services.Screen
import com.example.dynamicdiceprototype.utils.imageDTO_number_contentDescription

fun NavGraphBuilder.diceGraph(
    diceViewModel: DiceViewModel,
    navController: NavHostController,
    headerViewModel: HeaderViewModel
) {
  navigation(route = DicesScreen.Dices.route, startDestination = DicesScreen.DicesList.route) {
    composable(route = DicesScreen.DicesList.route) {
      LaunchedEffect(true) { headerViewModel.changeHeaderText("Dice") }
      TemplateSelectionScreen(
          dices = diceViewModel.dices.values.toList(),
          onSelectTemplate = {
            diceViewModel.selectDice(it)
            navController.navigate(Screen.MainScreen.route)
            headerViewModel.changeHeaderText(it.name)
          },
          onCreateNewDice = {
            diceViewModel.createNewDice()
            navController.navigate(DicesScreen.SelectFaces.route)
          },
          onCreateRandomDice = {
            diceViewModel.createRandomDice()
            navController.navigate(DicesScreen.EditDice.route)
          },
          onCreateNumberedDice = { start, end ->
            diceViewModel.createNumberedDice(start, end)
            navController.navigate(DicesScreen.EditDice.route)
          },
          menuActions =
              listOf(
                  MenuItem(
                      text = "Edit die",
                      callBack = {
                        try {
                          diceViewModel.editDice(it)
                          navController.navigate(DicesScreen.EditDice.route)
                        } catch (e: PermittedActionException) {
                          diceViewModel.toastMessageText = e.message
                        }
                      },
                  ),
                  MenuItem(
                      text = "Duplicate die",
                      callBack = { diceViewModel.duplicateDice(it) },
                  ),
                  MenuItem(
                      text = "Delete die",
                      callBack = {
                        try {
                          diceViewModel.removeDice(it)
                        } catch (e: PermittedActionException) {
                          diceViewModel.toastMessageText = e.message
                        }
                      },
                      alert =
                          AlterBoxProperties(
                              description =
                                  "Pressing Confirm will Delete the die and remove all occurences in any dice Group")),
              )) // TODO implement undoing feature, haha
    }
    composable(route = DicesScreen.SelectFaces.route) {
      LaunchedEffect(true) { headerViewModel.changeHeaderText("Set Faces and Values") }
      val facesWithNumber =
          diceViewModel.diceInEdit.faces
              .filter { it.contentDescription == imageDTO_number_contentDescription }
              .map {
                Pair(
                    ImageDTO(
                        contentDescription = imageDTO_number_contentDescription,
                        base64String = imageDTO_number_contentDescription),
                    it.value)
              }

      val selectableImages =
          diceViewModel.imageMap.values.filter { it.contentDescription != "image" }.toMutableList()
      selectableImages.addAll(0, facesWithNumber.map { it.first })

      val initialSelection =
          diceViewModel.diceInEdit.faces
              .filter { it.contentDescription != imageDTO_number_contentDescription }
              .associateBy(
                  { diceViewModel.imageMap[it.contentDescription] ?: ImageDTO() }, { it.value })
              .toMutableMap()
      initialSelection.putAll(facesWithNumber)

      SelectFacesScreen(
          faces = selectableImages,
          color = diceViewModel.diceInEdit.backgroundColor,
          initialValue = initialSelection,
          onFacesSelectionClick = {
            diceViewModel.setSelectedFaces(it)
            navController.navigate(DicesScreen.EditDice.route)
          })
    }
    composable(route = DicesScreen.EditDice.route) {
      LaunchedEffect(true) { headerViewModel.changeHeaderText("Edit Die") }
      EditDiceScreen(
          diceViewModel.diceInEdit,
          onEditFaces = { name, color ->
            diceViewModel.setDiceName(name)
            diceViewModel.setColor(color)
            navController.navigate(DicesScreen.SelectFaces.route)
          },
          showRerollButton = diceViewModel.showRerollButton,
          onRerollDice = { diceViewModel.createRandomDice() },
          onSaveDice = { name, color ->
            diceViewModel.setDiceName(name)
            diceViewModel.setColor(color)
            diceViewModel.saveDieInEdit()
            if (diceViewModel.dieEditMode == DieEditMode.EDIT_DIE_ROLL) {
              navController.navigate(Screen.MainScreen.route)
            } else {
              navController.navigate(DicesScreen.DicesList.route)
            }
          })
    }
  }
}
