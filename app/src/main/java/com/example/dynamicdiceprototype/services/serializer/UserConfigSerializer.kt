package com.example.dynamicdiceprototype.services.serializer

import androidx.datastore.core.Serializer
import com.example.dynamicdiceprototype.data.DTO.UserDTO
import java.io.InputStream
import java.io.OutputStream
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

object UserConfigSerializer : Serializer<UserDTO> {
  override val defaultValue: UserDTO
    get() = UserDTO()

  override suspend fun readFrom(input: InputStream): UserDTO {
    return try {
      Json.decodeFromString<UserDTO>(
          deserializer = UserDTO.serializer(), string = input.readBytes().decodeToString())
    } catch (e: SerializationException) {
      e.printStackTrace()
      defaultValue
    }
  }

  override suspend fun writeTo(t: UserDTO, output: OutputStream) {
    output.write(
        Json.encodeToString(serializer = UserDTO.serializer(), value = t).encodeToByteArray())
  }
}
