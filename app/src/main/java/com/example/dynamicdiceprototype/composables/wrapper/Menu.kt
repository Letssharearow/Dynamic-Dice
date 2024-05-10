package com.example.dynamicdiceprototype.composables.wrapper

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.dynamicdiceprototype.Screen
import com.example.dynamicdiceprototype.services.HeaderViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun Menu(drawerState: DrawerState, scope: CoroutineScope) {
  val navController = rememberNavController()
  val navItems =
      listOf(
          Pair("Test Screen", Screen.TestScreen.route),
          Pair("Main Screen", Screen.MainScreen.route),
          Pair("Create Dice", Screen.CreateDice.route),
          Pair("Dice Groups", Screen.DiceGroups.route),
          Pair("Create Dice Group", Screen.CreateDiceGroup.route),
          Pair("Upload Image", Screen.UploadImage.route),
      )

  ModalNavigationDrawer(
      drawerState = drawerState,
      drawerContent = {
        ModalDrawerSheet {
          Text("Navigate", fontWeight = FontWeight(550), modifier = Modifier.padding(16.dp))
          Divider()
          navItems.forEach { NavItem(navController, scope, drawerState, it.first, it.second) }
        }
      }) {
        NavGraph(navController)
      }
}

@Composable
fun NavItem(
    navController: NavHostController,
    scope: CoroutineScope,
    drawerState: DrawerState,
    name: String,
    route: String
) {
  val viewModel = viewModel<HeaderViewModel>()

  NavigationDrawerItem(
      label = { Text(text = name) },
      selected = false,
      onClick = {
        viewModel.changeHeaderText(name)
        navController.navigate(route)
        scope.launch { drawerState.close() }
      })
}
