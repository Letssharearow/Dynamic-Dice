package com.example.dynamicdiceprototype.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.dynamicdiceprototype.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun Menu(drawerState: DrawerState, scope: CoroutineScope) {
  val navController = rememberNavController()

  ModalNavigationDrawer(
      drawerState = drawerState,
      drawerContent = {
        ModalDrawerSheet {
          Text("Drawer title", modifier = Modifier.padding(16.dp))
          Divider()
          NavigationDrawerItem(
              label = { Text(text = "Main Screen") },
              selected = false,
              onClick = {
                navController.navigate(Screen.MainScreen.route)
                scope.launch { drawerState.close() }
              })
          Divider()
          NavigationDrawerItem(
              label = { Text(text = "Create Dice") },
              selected = false,
              onClick = {
                navController.navigate(Screen.CreateDice.route)
                scope.launch { drawerState.close() }
              })
          // ...other drawer items
        }
      }) {
        NavGraph(navController)
      }
}
