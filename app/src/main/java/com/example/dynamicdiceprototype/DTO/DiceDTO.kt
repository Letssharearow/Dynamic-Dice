package com.example.dynamicdiceprototype.DTO

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.Face
import com.example.dynamicdiceprototype.services.FirebaseDataStore
import kotlinx.serialization.Serializable

@Serializable
data class DiceDTO(
    val images: List<FaceDTO> = listOf(),
    var backgroundColor: Int = Color(0xFFCCCCCC).toArgb(),
    val name: String = "diceDTOName"
)

fun DiceDTO.toDice(id: String): Dice {
  val faces =
      this.images.map { image ->
        Face(
            contentDescription = image.contentDescription,
            weight = image.weight,
            value = image.value)
      }

  return Dice(
      id = id,
      name = name, // Replace with actual logic to determine the name
      faces = faces,
      backgroundColor = Color(this.backgroundColor))
}

fun DiceDTO.toDice(id: String, images: Map<String, ImageDTO>): Dice {
  val faces =
      this.images.map { image ->
        Face(
            contentDescription = image.contentDescription,
            weight = image.weight,
            value = image.value,
            data =
                images[image.contentDescription]?.let {
                  FirebaseDataStore.base64ToBitmap(it.base64String)
                })
      }

  return Dice(id = id, name = name, faces = faces, backgroundColor = Color(this.backgroundColor))
}
