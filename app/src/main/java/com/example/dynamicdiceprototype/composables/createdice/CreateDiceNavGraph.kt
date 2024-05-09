package com.example.dynamicdiceprototype.composables.createdice

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dynamicdiceprototype.Screen
import com.example.dynamicdiceprototype.data.Face
import com.example.dynamicdiceprototype.services.DiceViewModel

@Composable
fun CreateDiceNavGraph(diceViewModel: DiceViewModel) {
  val navController = rememberNavController()

  NavHost(navController, startDestination = Screen.Templates.route) {
    composable(route = Screen.Templates.route) {
      TemplateSelectionScreen(
          diceViewModel, onSelectTemplate = { navController.navigate(Screen.EditTemplate.route) }) {
            diceViewModel.setFaceSize(it)
            navController.navigate(Screen.SelectFaces.route)
          }
    }
    composable(route = Screen.SelectFaces.route) {
      SelectFacesScreen(
          faces = diceViewModel.imageMap.map { Face(data = it.value, imageId = it.key) },
          size = diceViewModel.facesSize,
          initialValue =
              diceViewModel.newDice.faces.associate {
                Pair(it.imageId, it)
              }) { // TODO Consider using same datatype (map probably) for everything)
            navController.navigate(Screen.EditTemplate.route)
            diceViewModel.setSelectedFaces(it.values)
          }
    }
    composable(route = Screen.EditTemplate.route) {
      EditDiceScreen(
          diceViewModel,
          onEdit = { navController.navigate(Screen.SelectFaces.route) },
          onSaveDice = {
            navController.navigate(Screen.Templates.route)
            diceViewModel.saveDice()
          })
    }
  }
}
