package com.example.dynamicdiceprototype.utils

import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.dynamicdiceprototype.DTO.ImageDTO
import com.example.dynamicdiceprototype.R
import com.example.dynamicdiceprototype.services.DiceViewModel
import com.example.dynamicdiceprototype.services.FirebaseDataStore

fun saveImages(res: Resources, viewModel: DiceViewModel) {
  data class ImageModelSetDTO(val image: Int, val name: String)
  val images =
      arrayOf<ImageModelSetDTO>(
          ImageModelSetDTO(image = R.drawable.cameleon, name = "Cameleon"),
          ImageModelSetDTO(image = R.drawable.elephant, name = "Elephant"),
          ImageModelSetDTO(image = R.drawable.fish, name = "Fish"),
          ImageModelSetDTO(image = R.drawable.frog, name = "Frog"),
          ImageModelSetDTO(image = R.drawable.lion, name = "Lion"),
          ImageModelSetDTO(image = R.drawable.monkey, name = "Monkey"),
          ImageModelSetDTO(image = R.drawable.owl, name = "Owl"),
          ImageModelSetDTO(image = R.drawable.parrot, name = "Parrot"),
          ImageModelSetDTO(image = R.drawable.penguin, name = "Penguin"),
          ImageModelSetDTO(image = R.drawable.one_transparent, name = "one"),
          ImageModelSetDTO(image = R.drawable.two_transparent, name = "two"),
          ImageModelSetDTO(image = R.drawable.three_transparent, name = "three"),
          ImageModelSetDTO(image = R.drawable.four_transparent, name = "four"),
          ImageModelSetDTO(image = R.drawable.five_transparent, name = "five"),
          ImageModelSetDTO(image = R.drawable.six_transparent, name = "six"),
          ImageModelSetDTO(image = R.drawable.wizard_blue, name = "Wizard Blue"),
          ImageModelSetDTO(image = R.drawable.wizard_green, name = "Wizard Green"),
          ImageModelSetDTO(image = R.drawable.wizard_red, name = "Wizard Red"),
          ImageModelSetDTO(image = R.drawable.wizard_yellow, name = "Wizard Yellow"),
          ImageModelSetDTO(image = R.drawable.wizard_narr, name = "wizard Narr"),
          ImageModelSetDTO(image = R.drawable.wizard_wizard, name = "wizard Wizard"),
      )

  viewModel.saveImages(
      images.map {
        ImageDTO(
            contentDescription = it.name,
            base64String =
                FirebaseDataStore.bitmapToBase64(BitmapFactory.decodeResource(res, it.image)))
      })

  val imageCreator = ImageCreator()

  // Assuming you have a list of colors
  val colors =
      listOf(
          Pair(Color.Red, "Red"),
          Pair(Color(0xFFFF7F00), "Orange"), // Orange
          Pair(Color.Yellow, "Yellow"),
          Pair(Color.Green, "Green"),
          Pair(Color.Blue, "Blue"),
          Pair(Color(0xFF4B0082), "Indigo"), // Indigo
          Pair(Color(0xFF8B00FF), "Violet"), // Violet
          // Additional colors
          Pair(Color(0xFFFFC0CB), "Pink"), // Pink
          Pair(Color(0xFFFFD700), "Gold"), // Gold
          Pair(Color.Cyan, "Cyan"),
          Pair(Color(0xFFFFA500), "Light orange"), // Light Orange
          Pair(Color(0xFF800080), "Purple"), // Purple
          Pair(Color.Magenta, "Magenta"),
          Pair(Color(0xFFC0C0C0), "Silver"), // Silver
          Pair(Color.Gray, "Gray"),
          Pair(Color.Black, "Black"),
          Pair(Color.White, "White"))

  viewModel.saveImages(
      colors.map { color ->
        val namedColor = color.second
        val bitmap = imageCreator.getBitmap(400, color.first.toArgb())
        ImageDTO(
            contentDescription = namedColor,
            base64String = FirebaseDataStore.bitmapToBase64(bitmap))
      })
}
