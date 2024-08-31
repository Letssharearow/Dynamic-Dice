package com.example.dynamicdiceprototype.services

private const val createDiceRoute = "dices"

sealed class DicesScreen(val route: String) {

  object Dices : DicesScreen(createDiceRoute)

  object DicesList : DicesScreen("$createDiceRoute/templates")

  object SelectFaces : DicesScreen("$createDiceRoute/faces")

  object EditDice : DicesScreen("$createDiceRoute/templates/edit")
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
