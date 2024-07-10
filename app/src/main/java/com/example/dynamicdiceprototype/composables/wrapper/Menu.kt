package com.example.dynamicdiceprototype.composables.wrapper

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.dynamicdiceprototype.composables.createdice.DicesScreen
import com.example.dynamicdiceprototype.services.DiceViewModel
import com.example.dynamicdiceprototype.services.HeaderViewModel
import com.example.dynamicdiceprototype.services.PreferenceKey
import com.example.dynamicdiceprototype.services.PreferenceManager
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Regular
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.regular.Save
import compose.icons.fontawesomeicons.solid.Dice
import compose.icons.fontawesomeicons.solid.DiceD6
import compose.icons.fontawesomeicons.solid.Image
import compose.icons.fontawesomeicons.solid.Images
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class NavItmProps(val text: String, val route: String, val imageVector: ImageVector)

@Composable
fun Menu(
    drawerState: DrawerState,
    scope: CoroutineScope,
    navController: NavHostController,
    viewModel: DiceViewModel
) {
  val widthFraction =
      PreferenceManager.getPreferenceFlow<Int>(PreferenceKey.MenuDrawerMaxWidthFraction)
          .collectAsState(initial = 75)
          .value
  val navItems =
      listOf(
          NavItmProps("Test Screen", Screen.TestScreen.route, Icons.Default.Info),
          NavItmProps("Main Screen", Screen.MainScreen.route, Icons.Default.Home),
          NavItmProps("Dices", DicesScreen.Dices.route, FontAwesomeIcons.Solid.DiceD6),
          NavItmProps("Dice Groups", Screen.DiceGroups.route, FontAwesomeIcons.Solid.Dice),
          NavItmProps("Add Images", Screen.SaveImage.route, FontAwesomeIcons.Solid.Image),
          NavItmProps("Images", Screen.Images.route, FontAwesomeIcons.Solid.Images),
      )
  ModalNavigationDrawer(
      drawerState = drawerState,
      drawerContent = {
        ModalDrawerSheet(modifier = Modifier.fillMaxWidth(widthFraction / 100f)) {
          Text("Navigate", fontWeight = FontWeight(550), modifier = Modifier.padding(16.dp))
          HorizontalDivider()
          navItems.forEach { NavItem(navController, scope, drawerState, it) }
        }
      },
  ) {
    NavGraph(navController, viewModel)
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
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(24.dp))
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

@Preview(showBackground = true)
@Composable
private fun Preview() {
  DynamicDicePrototypeTheme {
    Column {
      Icon(
          imageVector = FontAwesomeIcons.Regular.Save,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.size(24.dp))
      Icon(
          imageVector = Icons.Default.Info,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurface)
    }
  }
}
