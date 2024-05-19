package com.example.dynamicdiceprototype.composables.createdice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dynamicdiceprototype.Screen
import com.example.dynamicdiceprototype.composables.common.AlertBox
import com.example.dynamicdiceprototype.data.AlterBoxProperties
import com.example.dynamicdiceprototype.data.MenuItem
import com.example.dynamicdiceprototype.services.DiceViewModel

@Composable
fun CreateDiceNavGraph(diceViewModel: DiceViewModel) {
  val navController = rememberNavController()

  NavHost(navController, startDestination = Screen.Templates.route) {
    composable(route = Screen.Templates.route) {
      TemplateSelectionScreen(
          dices = diceViewModel.dices.values.toList(),
          onSelectTemplate = {
            diceViewModel.setStartDice(it)
            navController.navigate(Screen.EditTemplate.route)
          },
          onCreateNewDice = {
            diceViewModel.createNewDice(it)
            navController.navigate(Screen.SelectFaces.route)
          },
          menuActions =
              listOf(
                  MenuItem(
                      text = "Edit dice",
                      callBack = {
                        diceViewModel.editDice(it)
                        navController.navigate(Screen.EditTemplate.route)
                      },
                  ),
                  MenuItem(
                      text = "Duplicate dice",
                      callBack = {
                        diceViewModel.duplicateDice(it)
                        navController.navigate(Screen.EditTemplate.route)
                      },
                  ),
                  MenuItem(
                      text = "Delete dice",
                      callBack = { diceViewModel.removeDice(it) },
                      AlterBoxProperties(
                          description =
                              "Pressing Confirm will Delete the Dice entirely and globally, there is no undoing (yet)")),
              )) // TODO implement undoing feature, haha
    }
    composable(route = Screen.SelectFaces.route) {
      SelectFacesScreen(
          faces = diceViewModel.imageMap.values.toList(),
          size = diceViewModel.facesSize,
          initialValue =
              diceViewModel.diceInEdit.faces.associate {
                Pair(it.contentDescription, it)
              }) { // TODO Consider using same datatype (map probably) for everything)
            navController.navigate(Screen.EditTemplate.route)
            diceViewModel.setSelectedFaces(it.values)
          }
    }
    composable(route = Screen.EditTemplate.route) {
      var openDialog by remember { mutableStateOf(false) }
      EditDiceScreen(
          diceViewModel.diceInEdit,
          onEdit = { name, color ->
            diceViewModel.setDiceName(name)
            diceViewModel.setColor(color)
            navController.navigate(Screen.SelectFaces.route)
          },
          isEditMode = diceViewModel.isDiceEditMode,
          onSaveDice = { name, color ->
            diceViewModel.setDiceName(name)
            diceViewModel.setColor(color)
            if (!diceViewModel.isDiceEditMode && diceViewModel.dices.contains(name)) {
              openDialog = true
            } else {
              diceViewModel.saveDice()
              navController.navigate(Screen.Templates.route)
            }
          })
      AlertBox(
          isOpen = openDialog,
          text = "Name Already Exists, are you sure you want to overwrite it?",
          onDismiss = { openDialog = false },
          conConfirm = {
            openDialog = false
            diceViewModel.saveDice()
            navController.navigate(Screen.Templates.route)
          })
    }
  }
}
