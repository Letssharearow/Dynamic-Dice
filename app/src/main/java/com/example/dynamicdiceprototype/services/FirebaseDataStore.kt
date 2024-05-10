package com.example.dynamicdiceprototype.services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.dynamicdiceprototype.DTO.get.DiceGetDTO
import com.example.dynamicdiceprototype.DTO.get.ImageGetDTO
import com.example.dynamicdiceprototype.DTO.get.UserGetDTO
import com.example.dynamicdiceprototype.DTO.set.DiceSetDTO
import com.example.dynamicdiceprototype.DTO.set.ImageSetDTO
import com.example.dynamicdiceprototype.DTO.set.UserSetDTO
import com.example.dynamicdiceprototype.data.Face
import com.example.dynamicdiceprototype.data.ImageModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.io.ByteArrayOutputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

const val TAG = "MyApp"
private const val IMAGE_COLLECTION_NAME = "images"
private const val DICES_COLLECTION_NAME = "dices"
private const val CONFIG_COLLECTION_NAME = "users"
const val local = true

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

  val imagesFlow = flow {
    val map = mutableMapOf<String, ImageModel>()
    if (local) {
      emit(mockImages())
      return@flow
    }
    val collectionRef = db.collection(IMAGE_COLLECTION_NAME)
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

  val dicesFlow = flow {
    val map = mutableMapOf<String, Array<String>>()
    if (local) {
      emit(getMockDices())
      return@flow
    }
    val collectionRef = db.collection(DICES_COLLECTION_NAME)
    val documents = collectionRef.get().await()
    for (document in documents) {
      val documentId = document.id
      val documentData = document.data
      Log.d(TAG, "Firebase fetching data: $documentId => $documentData")
      val imageIds = documentData[DiceProperty.IMAGE_IDS.name] as? Array<String> ?: continue
      map[documentId] = imageIds
    }
    Log.d(TAG, "Firebase all data fetched (keys): ${map.keys}")
    emit(map)
  }

  val userFlow = flow {
    val userDTO = fetchUserData("juli")
    emit(userDTO)
  }

  fun uploadBitmap(key: String, image: ImageSetDTO) {
    val dataMap =
        hashMapOf(
            ImageProperty.IMAGE_BITMAP.name to bitmapToBase64(image.image),
            ImageProperty.CONTENT_DESCRIPTION.name to image.contentDescription)

    setDocument(key, dataMap, IMAGE_COLLECTION_NAME)
  }

  fun uploadDice(key: String, dice: DiceSetDTO) {
    val dataMap =
        hashMapOf(
            DiceProperty.IMAGE_IDS.name to dice.images,
            DiceProperty.COLOR.name to dice.backgroundColor,
        )
    setDocument(key, dataMap, DICES_COLLECTION_NAME)
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

  suspend fun fetchUserData(userId: String): UserGetDTO? =
      withContext(Dispatchers.IO) {
        val collectionConfig = db.collection(CONFIG_COLLECTION_NAME)
        try {
          val documentSnapshot = collectionConfig.document(userId).get().await()
          Log.d(TAG, "Firebase fetchUserData: ${documentSnapshot.id} => ${documentSnapshot.data}")
          if (documentSnapshot.exists()) {
            val diceGroups =
                documentSnapshot[UserProperty.DICE_GROUPS.name] as? Map<String, Map<String, Int>>
                    ?: emptyMap()
            val dicesName =
                documentSnapshot[UserProperty.DICES.name] as? List<String> ?: emptyList()
            val dices = getDicesFromIds(dicesName)
            return@withContext UserGetDTO(diceGroups = diceGroups, dices = dices)
          }
        } catch (exception: Exception) {
          Log.e(TAG, "ERROR ${exception.message}")
        }
        return@withContext null
      }

  private suspend fun getDicesFromIds(dicesName: List<String>): Map<String, DiceGetDTO> =
      withContext(Dispatchers.IO) {
        val collectionDices = db.collection(DICES_COLLECTION_NAME)
        val dicesList: MutableMap<String, DiceGetDTO> = mutableMapOf()

        dicesName.forEach { diceName ->
          try {
            val documentSnapshot = collectionDices.document(diceName).get().await()
            Log.d(
                TAG, "Firebase getDicesFromIds: ${documentSnapshot.id} => ${documentSnapshot.data}")
            if (documentSnapshot.exists()) {
              val imagesId =
                  documentSnapshot[DiceProperty.IMAGE_IDS.name] as? Map<String, Int> ?: emptyMap()
              val backgroundColor = documentSnapshot[DiceProperty.COLOR.name] as? Int ?: 0
              val images = getImagesFromIds(imagesId)
              dicesList[diceName] = DiceGetDTO(backgroundColor = backgroundColor, images = images)
            }
          } catch (exception: Exception) {
            Log.e(TAG, "ERROR ${exception.message}")
          }
        }
        Log.d(TAG, "Firebase dicesList $dicesList")
        return@withContext dicesList
      }

  private suspend fun getImagesFromIds(imagesId: Map<String, Int>): List<ImageGetDTO> {
    val collectionDices = db.collection(IMAGE_COLLECTION_NAME)
    val imagesList: MutableList<ImageGetDTO> = mutableListOf()
    imagesId.forEach { (imageId, weight) ->
      collectionDices
          .document(imageId)
          .get()
          .addOnSuccessListener { document ->
            Log.d(TAG, "Firebase getImagesFromIds: ${document.id} => ${document.data}")
            if (document.exists()) {
              val imageBase64String = document[ImageProperty.IMAGE_BITMAP.name] as? String ?: ""
              val contentDescription =
                  document[ImageProperty.CONTENT_DESCRIPTION.name] as? String ?: ""
              imagesList.add(
                  ImageGetDTO(
                      Face(
                          imageId = imageId,
                          weight = weight,
                          data =
                              ImageModel(
                                  contentDescription = contentDescription,
                                  imageBitmap = base64ToBitmap(imageBase64String)))))
            }
          }
          .addOnFailureListener { exception -> Log.e(TAG, "ERROR ${exception.message}") }
          .await()
    }
    return imagesList
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
