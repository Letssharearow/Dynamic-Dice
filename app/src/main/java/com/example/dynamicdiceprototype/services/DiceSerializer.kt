package com.example.dynamicdiceprototype.services

import androidx.datastore.core.Serializer
import com.example.dynamicdiceprototype.DTO.set.DiceSetDTO
import java.io.InputStream
import java.io.OutputStream
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

object DiceSerializer : Serializer<DiceSetDTO> {
  override val defaultValue: DiceSetDTO
    get() = DiceSetDTO()

  override suspend fun readFrom(input: InputStream): DiceSetDTO {
    return try {
      Json.decodeFromString<DiceSetDTO>(
          deserializer = DiceSetDTO.serializer(), string = input.readBytes().decodeToString())
    } catch (e: SerializationException) {
      e.printStackTrace()
      defaultValue
    }
  }

  override suspend fun writeTo(t: DiceSetDTO, output: OutputStream) {
    output.write(
        Json.encodeToString(serializer = DiceSetDTO.serializer(), value = t).encodeToByteArray())
  }
}
