package com.example.dynamicdiceprototype.services

import androidx.datastore.core.Serializer
import com.example.dynamicdiceprototype.DTO.set.DicesSetDTO
import java.io.InputStream
import java.io.OutputStream
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

object DiceSerializer : Serializer<DicesSetDTO> {
  override val defaultValue: DicesSetDTO
    get() = DicesSetDTO()

  override suspend fun readFrom(input: InputStream): DicesSetDTO {
    return try {
      Json.decodeFromString<DicesSetDTO>(
          deserializer = DicesSetDTO.serializer(), string = input.readBytes().decodeToString())
    } catch (e: SerializationException) {
      e.printStackTrace()
      defaultValue
    }
  }

  override suspend fun writeTo(t: DicesSetDTO, output: OutputStream) {
    output.write(
        Json.encodeToString(serializer = DicesSetDTO.serializer(), value = t).encodeToByteArray())
  }
}
