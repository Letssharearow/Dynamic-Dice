package com.example.dynamicdiceprototype.services

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamicdiceprototype.DTO.get.toDice
import com.example.dynamicdiceprototype.DTO.set.DiceSetDTO
import com.example.dynamicdiceprototype.DTO.set.ImageSetDTO
import com.example.dynamicdiceprototype.DTO.set.UserSetDTO
import com.example.dynamicdiceprototype.R
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.DiceState
import com.example.dynamicdiceprototype.data.Face
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

// extend ViewModel to survive configuration change (landscape mode)
object DiceViewModel : ViewModel() {
  val firebase = FirebaseDataStore()
  var currentDices by mutableStateOf(listOf<Dice>())
  var imageMap by mutableStateOf(mutableMapOf<String, Face>()) // TODO
  var collectFlows by mutableStateOf(0)

  // create Dice
  var newDice by mutableStateOf<Dice>(Dice(name = "Change Later"))
  var facesSize by mutableStateOf<Int>(20)
  val diceGroups = mutableStateMapOf<String, Map<String, Int>>()
  var dices = mutableStateMapOf<String, Dice>()
  var lastDiceGroup by mutableStateOf("Kniffel")

  fun getErrorMessage() = firebase.errorMessage

  fun addDice(dice: Dice) {
    dices[dice.name] = dice
    // TODO Store config locally
  }

  fun mapDiceIdsToImages(images: Map<String, Face>) {
    dices.forEach {
      it.value.faces.map { face -> face.data = images[face.contentDescription]?.data }
    }
    currentDices.forEach {
      it.faces.map { face -> face.data = images[face.contentDescription]?.data }
    }
  }

  // create Dice Flow

  fun copyDice(name: String): Dice {
    val diceState = dices[name]
    if (diceState != null) {
      return copyIfNotExists(diceState.copy(name = name + "_copy"))
    }
    return Dice(
        faces = listOf()) // TODO Better handling of error, probably throw exception? Or return null
  }

  fun copyIfNotExists(dice: Dice): Dice {
    return if (dices.contains(dice.name)) copyIfNotExists(dice.copy(name = dice.name + "_copy"))
    else dice
  }

  fun removeDice(dice: Dice) {
    dices.remove(dice.name)
  }

  fun setStartDice(newDice: Dice) {
    this.newDice = copyDice(newDice.name)
  }

  fun updateSelectedFaces(faces: Map<String, Face>) {
    newDice = newDice.copy(faces = faces.values.toList())
  }

  fun updateBackgroundColor(color: Color) {
    newDice = newDice.copy(backgroundColor = color)
  }

  fun setDiceName(name: String) {
    newDice.name = name
  }

  fun createNewDice(number: Int) {
    facesSize = number
    newDice = Dice(name = "Change Later")
  }

  fun setSelectedFaces(values: Collection<Face>) {
    newDice.faces = values.toList()
  }

  fun setColor(color: Color) {
    newDice.backgroundColor = color
  }

  fun saveDice() {
    addDice(newDice)
  }
  // end create dice

  fun createDiceGroup(name: String, dices: Map<String, Pair<Dice, Int>>) {
    diceGroups[name] = mapOf(*dices.map { Pair(it.key, it.value.second) }.toTypedArray())
  }

  init {
    loadUserConfig()
  }

  // Function to update a single dice
  fun lockDice(dice: Dice) {
    currentDices =
        // use Map function to trigger recomposition
        currentDices.map {
          if (it === dice) {
            // use copy function to trigger recomposition
            if (dice.state == DiceState.UNLOCKED) dice.copy(state = DiceState.LOCKED, rotation = 0F)
            else {
              dice.copy(state = DiceState.UNLOCKED, rotation = 0F)
            }
          } else it
        }
  }

  fun rollDices() {
    currentDices =
        currentDices.map { dice ->
          if (dice.state != DiceState.LOCKED) {
            dice.copy(current = null) // set null to trigger recomposition and roll in Dice class
          } else {
            dice
          }
        }
  }

  private fun loadUserConfig() {
    viewModelScope.launch {
      val userDTO = firebase.fetchUserData("juli")
      userDTO?.diceGroups?.forEach { diceGroups[it.key] = it.value } // TODO handle config null
      val tasks =
          userDTO?.dices?.map {
            dices[it] = Dice(name = it)
            async { loadDice(it) }
          }
      tasks?.awaitAll()
      collectFlows++
      collectFlows++
      selectDiceGroup(lastDiceGroup)
    }
  }

  private suspend fun loadDice(diceId: String) {
    val diceDTO = firebase.getDiceFromId(diceId)
    diceDTO?.let { diceGetDTO ->
      dices[diceId] = diceGetDTO.toDice(diceId)
      viewModelScope.launch {
        val tasks = diceGetDTO.images.map { async { loadImage(diceId = diceId, imageId = it.key) } }
        tasks.awaitAll()
        selectDiceGroup(lastDiceGroup)
      }
    }
  }

  private suspend fun loadImage(diceId: String, imageId: String) {
    val bitmap = firebase.getImageFromId(imageId)
    bitmap?.let { bitmapNotNull ->
      val diceToUpdate = dices[diceId]
      diceToUpdate?.let { dice ->
        dices[diceId] =
            dice.copy(
                faces =
                    dice.faces.map {
                      if (it.contentDescription == imageId) it.copy(data = bitmapNotNull) else it
                    })
      }
    }
  }

  fun loadAllImages() {
    //    if (imageMap.isNotEmpty()) return
    viewModelScope.launch {
      val images = firebase.loadAllImages()
      imageMap = images
    }
  }

  fun selectDiceGroup(groupId: String) {
    lastDiceGroup = groupId
    val newDicesState = mutableListOf<Dice>()
    diceGroups[groupId]?.forEach { idAndCount ->
      val diceToAdd = dices[idAndCount.key]
      diceToAdd?.let {
        for (i in 1..idAndCount.value) {
          newDicesState.add(diceToAdd)
        }
      } // TODO better handling for null Dice
    }
    currentDices = newDicesState
  }

  fun uploadImage(bitmap: Bitmap, name: String) {
    imageMap[name] =
        Face(
            contentDescription = name,
            data = bitmap.asImageBitmap()) // TODO make sure upload was successful
    firebase.uploadBitmap(name, ImageSetDTO(image = bitmap, contentDescription = name))
  }

  fun saveUser() {
    firebase.uploadUserConfig(
        "juli", UserSetDTO(dices = dices.map { it.key }, diceGroups = diceGroups))
    firebase.uploadDices(
        dices
            .map { (key, value) ->
              key to
                  DiceSetDTO(
                      images = value.faces.map { it.contentDescription to it.weight }.toMap(),
                      backgroundColor = value.backgroundColor.toArgb())
            }
            .toMap())
  }
}

fun getFaces(n: Int): List<Face> {
  val list = mutableListOf<Face>()
  for (i in 1..n) {
    list.add(Face(contentDescription = "${R.drawable.six_transparent}"))
  }
  return list
}

fun getDices(n: Int = 5): List<Dice> {
  val list = mutableListOf<Dice>()
  for (i in 1..n) {
    list.add(
        Dice(
            name = "6er",
            faces =
                listOf(
                    Face(contentDescription = "${R.drawable.one_transparent}"),
                    Face(contentDescription = "${R.drawable.two_transparent}"),
                    Face(contentDescription = "${R.drawable.three_transparent}"),
                    Face(contentDescription = "${R.drawable.four_transparent}"),
                    Face(contentDescription = "${R.drawable.five_transparent}"),
                    Face(contentDescription = "${R.drawable.six_transparent}"))))
  }
  return list
}
