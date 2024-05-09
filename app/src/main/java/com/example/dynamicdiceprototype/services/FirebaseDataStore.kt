package com.example.dynamicdiceprototype.services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.dynamicdiceprototype.data.ImageModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.io.ByteArrayOutputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

const val TAG = "MyApp"
private const val COLLECTION_NAME = "images"
const val local = false

enum class ImageProperty {
  CONTENT_DESCRIPTION,
  IMAGE_BITMAP
}

class FirebaseDataStore {
  private val db = Firebase.firestore

  val imagesFlow = flow {
    val map = mutableMapOf<String, ImageModel>()
    if (local) {
      emit(mockImages())
      return@flow
    }
    val collectionRef = db.collection(COLLECTION_NAME)
    val documents = collectionRef.get().await()
    for (document in documents) {
      val documentId = document.id
      val documentData = document.data
      Log.d(TAG, "Firebase fetching data: $documentId => $documentData")
      val base64String = documentData[ImageProperty.IMAGE_BITMAP.name] as? String ?: continue
      val name = documentData[ImageProperty.CONTENT_DESCRIPTION.name] as? String ?: continue
      map[documentId] =
          ImageModel(imageBitmap = base64ToBitmap(base64String), contentDescription = name)
    }
    Log.d(TAG, "Firebase all data fetched (keys): ${map.keys}")
    emit(map)
  }

  fun uploadBitmap(key: String, name: String, bitmap: Bitmap) {
    val base64String = bitmapToBase64(bitmap)
    val dataMap =
        hashMapOf(
            ImageProperty.IMAGE_BITMAP.name to base64String,
            ImageProperty.CONTENT_DESCRIPTION.name to name)
    Log.d(TAG, "Firebase save this: $key => $dataMap")
    db.collection(COLLECTION_NAME)
        .document(key)
        .set(dataMap)
        .addOnSuccessListener { Log.d(TAG, "Firebase DocumentSnapshot added with ID: $key") }
        .addOnFailureListener { e -> Log.w(TAG, "Firebase Error adding document", e) }
  }

  companion object {

    @OptIn(ExperimentalEncodingApi::class)
    fun base64ToBitmap(base64String: String): ImageBitmap {
      return try {
        val decodedBytes = Base64.decode(base64String)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        bitmap.asImageBitmap()
      } catch (e: Exception) {
        e.printStackTrace()
        Log.e(TAG, e.message ?: "No message")
        val decodedBytes = Base64.decode(mockImage)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        bitmap.asImageBitmap()
      }
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun bitmapToBase64(bitmap: Bitmap): String {
      val byteArrayOutputStream = ByteArrayOutputStream()
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
      val byteArray = byteArrayOutputStream.toByteArray()
      return Base64.encode(byteArray)
    }
  }
}
