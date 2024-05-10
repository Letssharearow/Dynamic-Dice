package com.example.dynamicdiceprototype

private val createDiceRoute = "dice/create"

sealed class Screen(val route: String) {

  object MainScreen : Screen("home")

  object TestScreen : Screen("Test")

  object CreateDice : Screen(createDiceRoute)

  object Templates : Screen("$createDiceRoute/templates")

  object SelectFaces : Screen("$createDiceRoute/faces")

  object EditTemplate : Screen("$createDiceRoute/templates/edit")

  object DiceGroups : Screen("dice_groups")

  object CreateDiceGroup : Screen("dice_groups/create")

  object UploadImage : Screen("upload")

  fun withArgs(vararg args: String): String {
    return buildString {
      append(route)
      args.forEach { append("/${it.replace(" ", "-").lowercase()}") }
    }
  }
}
