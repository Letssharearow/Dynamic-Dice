package com.example.dynamicdiceprototype.services.serializer

import androidx.datastore.core.Serializer
import com.example.dynamicdiceprototype.data.DTO.DiceDTO
import java.io.InputStream
import java.io.OutputStream
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

@Serializable data class DiceDTOMap(val dices: Map<String, DiceDTO> = emptyMap())

object DiceSerializer : Serializer<DiceDTOMap> {
  override val defaultValue: DiceDTOMap
    get() = DiceDTOMap()

  override suspend fun readFrom(input: InputStream): DiceDTOMap {
    return try {
      Json.decodeFromString<DiceDTOMap>(
          deserializer = DiceDTOMap.serializer(), string = input.readBytes().decodeToString())
    } catch (e: SerializationException) {
      e.printStackTrace()
      defaultValue
    }
  }

  override suspend fun writeTo(t: DiceDTOMap, output: OutputStream) {
    output.write(
        Json.encodeToString(serializer = DiceDTOMap.serializer(), value = t).encodeToByteArray())
  }
}
