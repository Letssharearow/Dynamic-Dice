package com.example.dynamicdiceprototype

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.io.ByteArrayOutputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

val TAG = "MyApp"
val STORENAME = "users"

class FirebaseDataStore {
  val db = Firebase.firestore
  var image by mutableStateOf("")

  init {}

  fun uploadBitmap(key: String, bitmap: Bitmap) {
    val saveImage = bitmapToBase64(bitmap)
    val asHashMap = hashMapOf(key to saveImage)
    Log.d(TAG, "save in firestore: $asHashMap")
    db.collection(STORENAME)
        .document(key)
        .set(asHashMap)
        .addOnSuccessListener { documentReference ->
          Log.d(TAG, "DocumentSnapshot added with ID: $documentReference")
        }
        .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
  }

  @OptIn(ExperimentalEncodingApi::class)
  fun bitmapToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encode(byteArray)
  }

  fun getBitMapFromDataStore(): String? {
    var returnValue: String? = null
    db.collection(STORENAME)
        .get()
        .addOnSuccessListener { result ->
          for (document in result) {
            Log.d(TAG, "${document.id} => ${document.data.values.first()}")
            image = "${document.data.values.first()}"
          }
        }
        .addOnFailureListener { exception -> Log.w(TAG, "Error getting documents.", exception) }
    return returnValue
  }
}

fun main() {
  FirebaseDataStore()
}
