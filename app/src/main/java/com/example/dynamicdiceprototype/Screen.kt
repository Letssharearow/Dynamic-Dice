package com.example.dynamicdiceprototype

private val createDiceRoute = "dice/create"

sealed class Screen(val route: String) {

  object CreateDice : Screen(createDiceRoute)

  object MainScreen : Screen("home")

  object Templates : Screen("$createDiceRoute/templates")

  object CreateNewTemplate : Screen("$createDiceRoute/newTemplate")

  object SelectFaces : Screen("$createDiceRoute/faces")

  object EditTemplate : Screen("$createDiceRoute/templates/edit")

  fun withArgs(vararg args: String): String {
    return buildString {
      append(route)
      args.forEach { append("/${it.replace(" ", "-").lowercase()}") }
    }
  }
}
