package com.example.dynamicdiceprototype.developer_area

import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.dynamicdiceprototype.R
import com.example.dynamicdiceprototype.data.DTO.ImageDTO
import com.example.dynamicdiceprototype.services.view_model.DiceViewModel
import com.example.dynamicdiceprototype.utils.ImageMapper

fun saveImages(res: Resources, viewModel: DiceViewModel) {
  data class ImageModelSetDTO(val image: Int, val name: String)
  val images =
      arrayOf<ImageModelSetDTO>(
          ImageModelSetDTO(image = R.drawable.frog, name = "Frog"),
          ImageModelSetDTO(image = R.drawable.lion, name = "Lion"),
          ImageModelSetDTO(image = R.drawable.monkey, name = "Monkey"),
          ImageModelSetDTO(image = R.drawable.one_transparent, name = "one"),
          ImageModelSetDTO(image = R.drawable.two_transparent, name = "two"),
          ImageModelSetDTO(image = R.drawable.three_transparent, name = "three"),
          ImageModelSetDTO(image = R.drawable.four_transparent, name = "four"),
          ImageModelSetDTO(image = R.drawable.five_transparent, name = "five"),
          ImageModelSetDTO(image = R.drawable.six_transparent, name = "six"),
      )

  viewModel.saveImages(
      images.map {
        ImageDTO(
            contentDescription = it.name,
            base64String = ImageMapper.bitmapToBase64(BitmapFactory.decodeResource(res, it.image)))
      })

  val imageMapper = ImageMapper()

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
        val bitmap = imageMapper.getBitmap(400, color.first.toArgb())
        ImageDTO(contentDescription = namedColor, base64String = ImageMapper.bitmapToBase64(bitmap))
      })
}
