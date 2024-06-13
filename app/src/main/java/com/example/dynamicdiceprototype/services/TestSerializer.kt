package com.example.dynamicdiceprototype.services

import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

@Serializable data class TestStorableObject(val text: String = "text")

object TestSerializer : Serializer<TestStorableObject> {
  override val defaultValue: TestStorableObject
    get() = TestStorableObject()

  override suspend fun readFrom(input: InputStream): TestStorableObject {
    return try {
      Json.decodeFromString<TestStorableObject>(
          deserializer = TestStorableObject.serializer(),
          string = input.readBytes().decodeToString())
    } catch (e: SerializationException) {
      e.printStackTrace()
      defaultValue
    }
  }

  override suspend fun writeTo(t: TestStorableObject, output: OutputStream) {
    output.write(
        Json.encodeToString(serializer = TestStorableObject.serializer(), value = t)
            .encodeToByteArray())
  }
}
