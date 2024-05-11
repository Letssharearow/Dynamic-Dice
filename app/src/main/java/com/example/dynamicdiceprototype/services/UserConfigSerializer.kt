package com.example.dynamicdiceprototype.services

import androidx.datastore.core.Serializer
import com.example.dynamicdiceprototype.DTO.set.UserSetDTO
import java.io.InputStream
import java.io.OutputStream
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

object UserConfigSerializer : Serializer<UserSetDTO> {
  override val defaultValue: UserSetDTO
    get() = UserSetDTO()

  override suspend fun readFrom(input: InputStream): UserSetDTO {
    return try {
      Json.decodeFromString<UserSetDTO>(
          deserializer = UserSetDTO.serializer(), string = input.readBytes().decodeToString())
    } catch (e: SerializationException) {
      e.printStackTrace()
      defaultValue
    }
  }

  override suspend fun writeTo(t: UserSetDTO, output: OutputStream) {
    output.write(
        Json.encodeToString(serializer = UserSetDTO.serializer(), value = t).encodeToByteArray())
  }
}
