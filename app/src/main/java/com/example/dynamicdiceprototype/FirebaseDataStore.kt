package com.example.dynamicdiceprototype

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.io.ByteArrayOutputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

val TAG = "MyApp"
val COLLECTIONNAME = "images"

class FirebaseDataStore {
  val db = Firebase.firestore
  var image by mutableStateOf<ImageBitmap?>(null)
  var images by mutableStateOf(mutableMapOf<String, ImageBitmap>())

  var configuration: Configuration = Configuration()

  init {}

  // Function to update the map with a new image
  //  fun updateImage(documentId: String, imageUrl: String) {
  //    images[documentId] =
  //        imageUrl // This will trigger recomposition for the UI elements observing 'images'
  //  }

  // Function to fetch and store the collection from Firebase
  fun fetchAndStoreCollection() {
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
            Log.d(TAG, "$documentId => $documentData")
            map[documentId] = base64ToBitmap(documentData).asImageBitmap()
          }
          images = map
          Log.i(TAG, "Images2 map from firebase ${map.keys}")
        }
        .addOnFailureListener { exception ->
          // Handle any errors here
        }
  }

  fun uploadBitmap(key: String, bitmap: Bitmap) {
    val saveImage = bitmapToBase64(bitmap) // TODO rename
    val asHashMap = hashMapOf(key to saveImage) // TODO rename
    Log.d(TAG, "save in firestore: $asHashMap")
    db.collection(COLLECTIONNAME)
        .document(key)
        .set(asHashMap)
        .addOnSuccessListener { documentReference ->
          Log.d(TAG, "DocumentSnapshot added with ID: $documentReference")
        }
        .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
  }

  @OptIn(kotlin.io.encoding.ExperimentalEncodingApi::class)
  private fun base64ToBitmap(base64String: String): Bitmap {
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

  fun getBitMapFromDataStore() {
    db.collection(COLLECTIONNAME)
        .get()
        .addOnSuccessListener { result ->
          for (document in result) {
            Log.d(TAG, "${document.id} => ${document.data.values.first()}")
            image = base64ToBitmap("${document.data.values.first()}").asImageBitmap()
            images.plus(document.id to document.data.values.first())
          }
          Log.i(TAG, "Images map from firebase $images")
        }
        .addOnFailureListener { exception -> Log.w(TAG, "Error getting documents.", exception) }
  }
}
