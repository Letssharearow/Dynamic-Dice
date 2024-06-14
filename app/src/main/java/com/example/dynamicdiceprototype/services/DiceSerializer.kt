package com.example.dynamicdiceprototype.services

import androidx.datastore.core.Serializer
import com.example.dynamicdiceprototype.DTO.DiceDTO
import java.io.InputStream
import java.io.OutputStream
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

object DiceSerializer : Serializer<DiceDTO> {
  override val defaultValue: DiceDTO
    get() = DiceDTO()

  override suspend fun readFrom(input: InputStream): DiceDTO {
    return try {
      Json.decodeFromString<DiceDTO>(
          deserializer = DiceDTO.serializer(), string = input.readBytes().decodeToString())
    } catch (e: SerializationException) {
      e.printStackTrace()
      defaultValue
    }
  }

  override suspend fun writeTo(t: DiceDTO, output: OutputStream) {
    output.write(
        Json.encodeToString(serializer = DiceDTO.serializer(), value = t).encodeToByteArray())
  }
}
