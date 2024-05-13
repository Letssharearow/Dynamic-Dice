package com.example.dynamicdiceprototype.services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.dynamicdiceprototype.DTO.get.DiceGetDTO
import com.example.dynamicdiceprototype.DTO.get.UserGetDTO
import com.example.dynamicdiceprototype.DTO.set.DiceSetDTO
import com.example.dynamicdiceprototype.DTO.set.ImageSetDTO
import com.example.dynamicdiceprototype.DTO.set.UserSetDTO
import com.example.dynamicdiceprototype.data.Face
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import java.io.ByteArrayOutputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.coroutines.tasks.await

const val TAG = "MyApp"
private const val IMAGE_COLLECTION_NAME = "images"
private const val DICES_COLLECTION_NAME = "dices"
private const val CONFIG_COLLECTION_NAME = "users"
const val local = false

enum class ImageProperty {
  CONTENT_DESCRIPTION,
  IMAGE_BITMAP
}

enum class DiceProperty {
  IMAGE_IDS,
  COLOR
}

enum class UserProperty {
  DICE_GROUPS,
  DICES,
}

class FirebaseDataStore {
  private val db = Firebase.firestore
  var errorMessage by mutableStateOf<String?>(null)

  suspend fun loadAllImages(): MutableMap<String, Face> {
    val map = mutableMapOf<String, Face>()
    val collectionRef = db.collection(IMAGE_COLLECTION_NAME)
    val documents =
        collectionRef
            .get()
            .addOnFailureListener {
              it.printStackTrace()
              Log.e(TAG, "Firebase $IMAGE_COLLECTION_NAME")
              errorMessage = "Fetching Images"
            }
            .await()
    for (document in documents) {
      val documentId = document.id
      val documentData = document.data
      Log.d(TAG, "Firebase fetching data: $documentId => $documentData")
      val base64String = documentData[ImageProperty.IMAGE_BITMAP.name] as? String ?: continue
      val name = documentData[ImageProperty.CONTENT_DESCRIPTION.name] as? String ?: continue
      map[documentId] = Face(data = base64ToBitmap(base64String), contentDescription = name)
    }
    Log.d(TAG, "Firebase all data fetched (keys): ${map.keys}")
    return map
  }

  fun uploadBitmap(key: String, image: ImageSetDTO) {
    val dataMap =
        hashMapOf(
            ImageProperty.IMAGE_BITMAP.name to bitmapToBase64(image.image),
            ImageProperty.CONTENT_DESCRIPTION.name to image.contentDescription)

    setDocument(image.contentDescription, dataMap, IMAGE_COLLECTION_NAME)
  }

  fun uploadDice(key: String, dice: DiceSetDTO) {
    val dataMap =
        hashMapOf(
            DiceProperty.IMAGE_IDS.name to dice.images,
            DiceProperty.COLOR.name to dice.backgroundColor,
        )
    setDocument(key, dataMap, DICES_COLLECTION_NAME)
  }

  fun uploadDices(mapOf: Map<String, DiceSetDTO>) {
    mapOf.forEach { uploadDice(key = it.key, dice = it.value) }
  }

  fun uploadUserConfig(
      userId: String,
      user: UserSetDTO,
  ) {
    val dataMap =
        hashMapOf(
            UserProperty.DICE_GROUPS.name to user.diceGroups,
            UserProperty.DICES.name to user.dices,
        )
    setDocument(keyName = userId, dataMap = dataMap, collectionName = CONFIG_COLLECTION_NAME)
  }

  private suspend inline fun <reified T> fetchDocumentData(
      collectionName: String,
      documentId: String,
      crossinline mapper: (DocumentSnapshot) -> T?
  ): T? {
    val collection = db.collection(collectionName)
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

  suspend fun fetchUserData(userId: String): UserGetDTO? {
    return fetchDocumentData(CONFIG_COLLECTION_NAME, userId) { documentSnapshot ->
      val diceGroups =
          documentSnapshot[UserProperty.DICE_GROUPS.name] as? Map<String, Map<String, Int>>
      val dicesName = documentSnapshot[UserProperty.DICES.name] as? List<String>
      if (diceGroups != null && dicesName != null) {
        UserGetDTO(diceGroups = diceGroups, dices = dicesName)
      } else {
        null
      }
    }
  }

  suspend fun getDiceFromId(diceName: String): DiceGetDTO? {
    return fetchDocumentData(DICES_COLLECTION_NAME, diceName) { documentSnapshot ->
      val imagesId = documentSnapshot[DiceProperty.IMAGE_IDS.name] as? Map<String, Int>
      val backgroundColor = documentSnapshot[DiceProperty.COLOR.name] as? Number
      if (imagesId != null && backgroundColor != null) {
        DiceGetDTO(backgroundColor = backgroundColor.toInt(), images = imagesId)
      } else {
        null
      }
    }
  }

  suspend fun getImageFromId(imageId: String): ImageBitmap? {
    return fetchDocumentData(IMAGE_COLLECTION_NAME, imageId) { documentSnapshot ->
      (documentSnapshot[ImageProperty.IMAGE_BITMAP.name] as? String)?.let { base64ToBitmap(it) }
    }
  }

  private fun setDocument(
      keyName: String,
      dataMap: Any,
      collectionName:
          String // TODO make a class or something like with the Screen.paths.route and not a
      // string
  ) {
    Log.d(TAG, "Firebase save this: $keyName => $dataMap")
    db.collection(collectionName)
        .document(keyName)
        .set(dataMap)
        .addOnSuccessListener { Log.d(TAG, "Firebase DocumentSnapshot added with ID: $keyName") }
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
