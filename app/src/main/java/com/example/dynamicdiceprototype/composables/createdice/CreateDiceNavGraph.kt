package com.example.dynamicdiceprototype.composables.createdice

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.dynamicdiceprototype.Exceptions.PermittedActionException
import com.example.dynamicdiceprototype.Screen
import com.example.dynamicdiceprototype.composables.common.AlertBox
import com.example.dynamicdiceprototype.data.AlterBoxProperties
import com.example.dynamicdiceprototype.data.Face
import com.example.dynamicdiceprototype.data.MenuItem
import com.example.dynamicdiceprototype.services.DiceViewModel
import com.example.dynamicdiceprototype.services.FirebaseDataStore
import com.example.dynamicdiceprototype.services.HeaderViewModel

fun NavGraphBuilder.diceGraph(
    diceViewModel: DiceViewModel,
    navController: NavHostController,
    headerViewModel: HeaderViewModel
) {
  val test = ""
  navigation(route = DicesScreen.Dices.route, startDestination = DicesScreen.DicesList.route) {
    composable(route = DicesScreen.DicesList.route) {
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
            headerViewModel.changeHeaderText("Select faces of Dice")
          },
          menuActions =
              listOf(
                  MenuItem(
                      text = "Edit dice",
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
                      text = "Duplicate dice",
                      callBack = { diceViewModel.duplicateDice(it) },
                  ),
                  MenuItem(
                      text = "Delete dice",
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
                                  "Pressing Confirm will Delete the Dice and remove all occurences in any dice Group")),
              )) // TODO implement undoing feature, haha
    }
    composable(route = DicesScreen.SelectFaces.route) {
      if (diceViewModel.imageMap.isEmpty()) {
        diceViewModel.loadAllImages()
      }
      SelectFacesScreen(
          faces =
              diceViewModel.imageMap.values.toList().map {
                Face(
                    data = FirebaseDataStore.base64ToBitmap(it.base64String),
                    contentDescription = it.contentDescription)
              },
          size = diceViewModel.facesSize,
          color = diceViewModel.diceInEdit.backgroundColor,
          initialValue =
              diceViewModel.diceInEdit.faces.associateBy {
                it.contentDescription
              }) { // TODO Consider using same datatype (map probably) for everything)
            navController.navigate(DicesScreen.EditDice.route)
            diceViewModel.setSelectedFaces(it.values)
            headerViewModel.changeHeaderText("Make Final changes")
          }
    }
    composable(route = DicesScreen.EditDice.route) {
      var openDialog by remember { mutableStateOf(false) }
      EditDiceScreen(
          diceViewModel.diceInEdit,
          onEdit = { name, color ->
            diceViewModel.setDiceName(name)
            diceViewModel.setColor(color)
            navController.navigate(DicesScreen.SelectFaces.route)
          },
          isEditMode = diceViewModel.isDiceEditMode,
          onSaveDice = { name, color ->
            diceViewModel.setDiceName(name)
            diceViewModel.setColor(color)
            if (!diceViewModel.isDiceEditMode && diceViewModel.dices.contains(name)) {
              openDialog = true
            } else {
              diceViewModel.saveDice()
              navController.navigate(DicesScreen.DicesList.route)
            }
          })
      AlertBox(
          isOpen = openDialog,
          text = "Name Already Exists, are you sure you want to overwrite it?",
          onDismiss = { openDialog = false },
          conConfirm = {
            openDialog = false
            diceViewModel.saveDice()
            navController.navigate(DicesScreen.DicesList.route)
          })
    }
  }
}

private const val createDiceRoute = "dices"

sealed class DicesScreen(val route: String) {

  object Dices : DicesScreen(createDiceRoute)

  object DicesList : DicesScreen("$createDiceRoute/templates")

  object SelectFaces : DicesScreen("$createDiceRoute/faces")

  object EditDice : DicesScreen("$createDiceRoute/templates/edit")
}
