package com.example.dynamicdiceprototype

sealed class Screen(val route: String) {
  object CreateDice : Screen("dice/new")

  object MainScreen : Screen("home")

  fun withArgs(vararg args: String): String {
    return buildString {
      append(route)
      args.forEach { append("/${it.replace(" ", "-").lowercase()}") }
    }
  }
}
