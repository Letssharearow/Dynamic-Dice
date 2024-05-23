package com.example.dynamicdiceprototype.composables.wrapper

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.dynamicdiceprototype.Screen
import com.example.dynamicdiceprototype.composables.createdice.DicesScreen
import com.example.dynamicdiceprototype.services.HeaderViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class NavItmProps(val text: String, val route: String, val imageVector: ImageVector)

@Composable
fun Menu(drawerState: DrawerState, scope: CoroutineScope) {
  val navController = rememberNavController()
  val navItems =
      listOf(
          NavItmProps("Test Screen", Screen.TestScreen.route, Icons.Default.Info),
          NavItmProps("Main Screen", Screen.MainScreen.route, Icons.Default.Home),
          NavItmProps("Dices", DicesScreen.Dices.route, Icons.Default.Create),
          NavItmProps("Dice Groups", Screen.DiceGroups.route, Icons.Filled.Create),
          NavItmProps("Upload Image", Screen.UploadImage.route, Icons.Rounded.Create),
      )

  ModalNavigationDrawer(
      drawerState = drawerState,
      drawerContent = {
        ModalDrawerSheet {
          Text("Navigate", fontWeight = FontWeight(550), modifier = Modifier.padding(16.dp))
          HorizontalDivider()
          navItems.forEach { NavItem(navController, scope, drawerState, it) }
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
    props: NavItmProps,
) {
  val viewModel = viewModel<HeaderViewModel>()

  NavigationDrawerItem(
      icon = {
        Icon(
            imageVector = props.imageVector,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface)
      },
      label = { Text(text = props.text) },
      selected = false,
      onClick = {
        viewModel.changeHeaderText(props.text)
        navController.navigate(props.route)
        scope.launch { drawerState.close() }
      },
      modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding))
}
