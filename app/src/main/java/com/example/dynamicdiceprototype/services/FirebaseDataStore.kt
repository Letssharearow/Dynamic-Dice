package com.example.dynamicdiceprototype.services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.io.ByteArrayOutputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

val TAG = "MyApp"
val COLLECTIONNAME = "images"

class FirebaseDataStore {
  val db = Firebase.firestore
  val imagesFlow =
      flow<Map<String, ImageBitmap>> {
        val map = mutableMapOf<String, ImageBitmap>()
        val collectionRef = Firebase.firestore.collection(COLLECTIONNAME)
        collectionRef
            .get()
            .addOnSuccessListener { documents ->
              for (document in documents) {
                val documentId = document.id
                val documentData =
                    document.data[documentId] as? String
                        ?: continue // Replace with your actual field name
                Log.d(TAG, "Firebase fetching data: $documentId => $documentData")
                map[documentId] = base64ToBitmap(documentData).asImageBitmap()
              }
              Log.d(TAG, "Firebase all data fetched (keys): ${map.keys}")
            }
            .addOnFailureListener { exception ->
              // Handle any errors here
            }
            .await()
        emit(map)
      }

  fun uploadBitmap(key: String, name: String, bitmap: Bitmap) {
    val saveImage = bitmapToBase64(bitmap) // TODO rename
    val asHashMap = hashMapOf(name to saveImage) // TODO rename
    Log.d(TAG, "Firebase save this: $key => $asHashMap")
    db.collection(COLLECTIONNAME)
        .document(key)
        .set(asHashMap)
        .addOnSuccessListener { documentReference ->
          Log.d(TAG, "Firebase DocumentSnapshot added with ID: $documentReference")
        }
        .addOnFailureListener { e -> Log.w(TAG, "Firebase Error adding document", e) }
  }

  @OptIn(ExperimentalEncodingApi::class)
  fun base64ToBitmap(base64String: String): Bitmap {
    val decodedBytes = Base64.decode(base64String)
    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
  }

  @OptIn(ExperimentalEncodingApi::class)
  private fun bitmapToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encode(byteArray)
  }
}
