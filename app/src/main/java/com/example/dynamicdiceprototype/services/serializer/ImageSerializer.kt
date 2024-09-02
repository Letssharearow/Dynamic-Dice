package com.example.dynamicdiceprototype.services.serializer

import androidx.datastore.core.Serializer
import com.example.dynamicdiceprototype.data.DTO.ImageDTO
import java.io.InputStream
import java.io.OutputStream
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

@Serializable data class ImageDTOMap(val images: Map<String, ImageDTO> = emptyMap())

object ImageSerializer : Serializer<ImageDTOMap> {
  override val defaultValue: ImageDTOMap
    get() = ImageDTOMap()

  override suspend fun readFrom(input: InputStream): ImageDTOMap {
    return try {
      Json.decodeFromString<ImageDTOMap>(
          deserializer = ImageDTOMap.serializer(), string = input.readBytes().decodeToString())
    } catch (e: SerializationException) {
      e.printStackTrace()
      defaultValue
    }
  }

  override suspend fun writeTo(t: ImageDTOMap, output: OutputStream) {
    output.write(
        Json.encodeToString(serializer = ImageDTOMap.serializer(), value = t).encodeToByteArray())
  }
}
