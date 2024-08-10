package com.example.dynamicdiceprototype.services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.dynamicdiceprototype.DTO.DiceDTO
import com.example.dynamicdiceprototype.DTO.ImageDTO
import com.example.dynamicdiceprototype.DTO.UserDTO
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import java.io.ByteArrayOutputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.coroutines.tasks.await

const val TAG = "MyApp"
const val USER = "Juli Maintainance"
const val USER_FETCH = "Juli Maintainance"

enum class Collection {
  images,
  dices,
  users
}

class FirebaseDataStore {
  private val db = Firebase.firestore
  var errorMessage by mutableStateOf<String?>(null)

  suspend fun loadAllImages(): MutableMap<String, ImageDTO> {
    val map = mutableMapOf<String, ImageDTO>()
    val collection = Collection.images.name
    val collectionRef = db.collection(collection)
    val documents =
        collectionRef
            .get()
            .addOnFailureListener {
              it.printStackTrace()
              Log.e(TAG, "Firebase $collection")
              errorMessage = "Fetching Images"
            }
            .await()
    for (document in documents) {
      val documentId = document.id
      val imageDTO = document.toObject<ImageDTO>()
      map[documentId] = imageDTO
    }
    Log.d(TAG, "Firebase all data fetched (keys): ${map.keys}")
    return map
  }

  fun uploadImageDTO(
      image: ImageDTO,
      onSuccess: (String) -> Unit,
  ) {
    setDocument(image.contentDescription, image, Collection.images, onSuccess = onSuccess)
  }

  fun uploadImageDTOs(
      images: List<ImageDTO>,
      onSuccess: (String) -> Unit,
  ) {
    for (image in images) {
      uploadImageDTO(image = image, onSuccess = onSuccess)
    }
  }

  fun uploadDice(
      key: String,
      dice: DiceDTO,
      onSuccess: (String) -> Unit,
  ) {
    setDocument(key, dice, Collection.dices, onSuccess = onSuccess)
  }

  fun uploadDices(
      mapOf: Map<String, DiceDTO>,
      onSuccess: (String) -> Unit,
  ) {
    mapOf.forEach { uploadDice(key = it.key, dice = it.value, onSuccess = onSuccess) }
  }

  fun uploadUserConfig(
      userId: String,
      user: UserDTO,
      onSuccess: (String) -> Unit,
  ) {
    setDocument(
        keyName = userId, dataMap = user, collectionName = Collection.users, onSuccess = onSuccess)
  }

  private suspend inline fun <reified T> fetchDocumentData(
      collectionName: Collection,
      documentId: String,
      crossinline mapper: (DocumentSnapshot) -> T?
  ): T? {
    val collection = db.collection(collectionName.name)
    try {
      val documentSnapshot = collection.document(documentId).get().await()
      Log.d(TAG, "Firebase fetch: ${documentSnapshot.id} => ${documentSnapshot.data}")
      if (documentSnapshot.exists()) {
        return mapper(documentSnapshot)
      }
    } catch (exception: Exception) {
      exception.printStackTrace()
      Log.e(TAG, "ERROR ${exception.message}")
      errorMessage = "Fetching $collectionName"
    }
    return null
  }

  suspend fun fetchUserData(userId: String): UserDTO? {
    return fetchDocumentData(Collection.users, userId) { documentSnapshot ->
      documentSnapshot.toObject<UserDTO>()
    }
  }

  suspend fun getDiceFromId(diceName: String): DiceDTO? {
    return fetchDocumentData(Collection.dices, diceName) { documentSnapshot ->
      documentSnapshot.toObject<DiceDTO>()
    }
  }

  suspend fun getImageFromId(imageId: String): ImageDTO? {
    return fetchDocumentData(Collection.images, imageId) { documentSnapshot ->
      documentSnapshot.toObject<ImageDTO>()
    }
  }

  fun deleteDice(id: String) {
    db.collection(Collection.dices.name)
        .document(id)
        .delete()
        .addOnSuccessListener { Log.d(TAG, "Firebase DocumentSnapshot deleted with ID: $id") }
        .addOnFailureListener {
          Log.d(TAG, "Firebase DocumentSnapshot deleted failed, ID: $id")
          errorMessage = "Firebase DocumentSnapshot deleted failed, ID: $id, ${it.message}"
        }
  }

  private fun setDocument(
      keyName: String,
      dataMap: Any,
      collectionName: Collection,
      onSuccess: (String) -> Unit,
  ) {
    Log.d(TAG, "Firebase save this: $keyName => $dataMap")
    db.collection(collectionName.name)
        .document(keyName)
        .set(dataMap)
        .addOnSuccessListener {
          Log.d(TAG, "Firebase DocumentSnapshot added with ID: $keyName \nit: $it")
          onSuccess(keyName)
        }
        .addOnFailureListener { e ->
          Log.w(TAG, "Firebase Error adding document", e)
          errorMessage = "upload failed, error message: ${e.message}"
        }
  }

  companion object {

    @OptIn(ExperimentalEncodingApi::class)
    fun base64ToBitmap(base64String: String): ImageBitmap? {
      return try {
        val decodedBytes = Base64.decode(base64String)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        bitmap.asImageBitmap()
      } catch (e: Exception) {
        e.printStackTrace()
        Log.e(TAG, e.message ?: "No message")
        null
        //        val decodedBytes = Base64.decode(mockImage)
        //        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        //        bitmap.asImageBitmap()
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
