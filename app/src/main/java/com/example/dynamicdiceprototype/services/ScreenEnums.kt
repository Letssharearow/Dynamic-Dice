package com.example.dynamicdiceprototype.services

private const val CREATE_DICE_ROUTE = "dices"

sealed class DicesScreen(val route: String) {

  object Dices : DicesScreen(CREATE_DICE_ROUTE)

  object DicesList : DicesScreen("$CREATE_DICE_ROUTE/templates")

  object SelectFaces : DicesScreen("$CREATE_DICE_ROUTE/faces")

  object EditDice : DicesScreen("$CREATE_DICE_ROUTE/templates/edit")
}

sealed class Screen(val route: String) {

  object Onboarding : Screen("onboarding")

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
