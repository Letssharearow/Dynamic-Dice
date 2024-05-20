package com.example.dynamicdiceprototype.services

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamicdiceprototype.DTO.ImageSetDTO
import com.example.dynamicdiceprototype.DTO.UserDTO
import com.example.dynamicdiceprototype.DTO.toDice
import com.example.dynamicdiceprototype.Exceptions.DiceGroupNotFoundException
import com.example.dynamicdiceprototype.Exceptions.DiceNotFoundException
import com.example.dynamicdiceprototype.Exceptions.PermittedActionException
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.DiceState
import com.example.dynamicdiceprototype.data.Face
import com.example.dynamicdiceprototype.data.toDiceGetDTO
import kotlin.random.Random
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

// extend ViewModel to survive configuration change (landscape mode)
object DiceViewModel : ViewModel() {
  val firebase = FirebaseDataStore()
  var currentDices by mutableStateOf(listOf<Dice>())
  var imageMap by mutableStateOf(mutableMapOf<String, Face>()) // TODO fix warning
  var collectFlows by mutableStateOf(0)

  // create Dice
  var diceInEdit by mutableStateOf<Dice>(Dice()) // TODO make nullable
  var facesSize by mutableStateOf<Int>(6)
  var isDiceEditMode by mutableStateOf<Boolean>(false)

  // create Dice Group
  var groupInEdit by mutableStateOf<Pair<String, Map<String, Pair<Dice, Int>>>?>(null)
  var groupSize by mutableStateOf<Int>(6)
  var isGroupEditMode by mutableStateOf<Boolean>(false)

  // User Config
  val diceGroups = mutableStateMapOf<String, Map<String, Int>>()
  var userConfigIsNull: Boolean = false
  var dices = mutableStateMapOf<String, Dice>()
  var lastDiceGroup by mutableStateOf("Red flag or Green flag")

  var toastMessageText by mutableStateOf<String?>(null)

  init {
    loadUserConfig()
  }

  private fun generateUniqueName(baseName: String, keys: List<String>): String {
    var uniqueName = baseName
    while (keys.contains(uniqueName)) {
      uniqueName += "_copy"
    }
    return uniqueName
  }

  fun createNewGroup() {
    groupInEdit = null
    isGroupEditMode = false
  }

  // Dice

  // create Dice Flow

  private fun getDiceWithUniqueName(dice: Dice, names: List<String>): Dice {
    val diceName = generateUniqueName(dice.name, names)
    return dice.copy(name = diceName)
  }

  fun setDiceName(name: String) {
    diceInEdit.name = name
  }

  fun createNewDice() {
    diceInEdit = Dice(name = "Change Later")
    isDiceEditMode = false
  }

  fun setSelectedFaces(values: Collection<Face>) {
    diceInEdit.faces = values.toList()
  }

  fun setColor(color: Color) {
    diceInEdit.backgroundColor = color
  }

  private fun addDice(dice: Dice) {
    dices[dice.name] = dice
    firebase.uploadDice(dice.name, dice.toDiceGetDTO())
    saveUser()
  }

  fun saveDice() {
    addDice(
        diceInEdit) // TODO consider using events to set and update local data instead of doing it
    // locally and with firebase to avoid data inconsistencies
  }
  // end create dice

  // Dice Menu Actions

  fun removeKeyFromInnerMaps(
      diceGroups: MutableMap<String, Map<String, Int>>,
      keyToRemove: String
  ) {
    diceGroups.keys.forEach { groupKey ->
      diceGroups[groupKey] = diceGroups[groupKey]?.filterKeys { it != keyToRemove } ?: emptyMap()
    }
  }

  val nonMutableDices = listOf("6er")

  fun removeDice(dice: Dice) {
    if (nonMutableDices.contains(dice.name))
        throw PermittedActionException("Can not make changes to Dice: ${dice.name}")
    dices.remove(dice.name)
    removeKeyFromInnerMaps(diceGroups, dice.name)
    saveUser()
  }

  fun editDice(dice: Dice) {
    if (nonMutableDices.contains(dice.name))
        throw PermittedActionException("Can not make changes to Dice: ${dice.name}")
    diceInEdit = dice
    isDiceEditMode = true
  }

  fun duplicateDice(it: Dice) {
    val newDice = getDiceWithUniqueName(it, dices.keys.toList())
    addDice(newDice)
  }

  // Dice Menu Actions end

  fun selectDice(dice: Dice) {
    currentDices = listOf(dice)
  }

  // Dice end

  // Dice Group
  // create Dice Group
  fun createDiceGroup(name: String, dices: Map<String, Pair<Dice, Int>>) {
    diceGroups[name] = mapOf(*dices.map { Pair(it.key, it.value.second) }.toTypedArray())
    saveUser()
  }

  fun copyDiceGroup(name: String): Pair<String, Map<String, Int>> {
    val state = diceGroups[name] ?: throw DiceGroupNotFoundException("Group not found: $name")
    return copyDiceGroupIfNotExists(name.plus("_copy"), state.toMap())
  }

  fun copyDiceGroupIfNotExists(
      newName: String,
      state: Map<String, Int>
  ): Pair<String, Map<String, Int>> {
    val uniqueName = generateUniqueName(newName, diceGroups.keys.toList())
    return Pair(uniqueName, state)
  }
  // create Dice Group end
  // group Menu Actions
  fun removeGroup(it: String) {
    diceGroups.remove(it)
    saveUser()
  }

  fun editGroup(groupId: String) {
    isGroupEditMode = true
    val group = diceGroups[groupId]
    group?.let { group ->
      val group2 =
          mapOf(
              *group
                  .map {
                    Pair(
                        it.key,
                        Pair(
                            dices[it.key]
                                ?: throw DiceNotFoundException(
                                    "Dice with key: ${it.key} doesnt exist, create Dice to make this action"),
                            it.value))
                  }
                  .toTypedArray()) // TODO improve dataStructure? And remove assert for better
      // handling?
      groupInEdit = Pair(groupId, group2)
    }
  }

  fun duplicateGroup(gorupId: String) {
    val newDiceGroup = copyDiceGroup(gorupId)
    diceGroups[newDiceGroup.first] = newDiceGroup.second
  }
  // group Menu Actions end
  fun selectDiceGroup(groupId: String) {
    lastDiceGroup = groupId
    val newDicesState = mutableListOf<Dice>()
    diceGroups[groupId]?.forEach { (diceId, count) ->
      val diceToAdd = dices[diceId]
      diceToAdd?.let {
        for (i in 1..count) {
          newDicesState.add(diceToAdd.copy(rotation = Random.nextFloat() * i))
        }
      } // TODO better handling for null Dice
    }
    currentDices = newDicesState
  }
  // Dice Group end

  // Main Screen actions

  fun duplicateCurrentDice(it: Dice) {
    val mutableCurrentDices = currentDices.toMutableList()
    mutableCurrentDices.add(getDiceWithUniqueName(it, currentDices.map { it.name }))
    currentDices = mutableCurrentDices
  }

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
            dice.roll()
          } else {
            dice
          }
        }
  }

  // Main Screen actions end

  // Firebase Access
  fun getErrorMessage() = firebase.errorMessage

  private fun loadUserConfig() {
    viewModelScope.launch {
      val userDTO = firebase.fetchUserData(USER)
      if (userDTO != null) {
        userDTO.diceGroups.forEach { diceGroups[it.key] = it.value } // TODO handle config null
        val tasks =
            userDTO.dices.map {
              dices[it] = Dice(name = it)
              async { loadDice(it) }
            }
        tasks.awaitAll()
        collectFlows++
        collectFlows++
        selectDiceGroup(lastDiceGroup)
      } else {
        userConfigIsNull = true
      }
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
    viewModelScope.launch {
      val images = firebase.loadAllImages()
      imageMap = images
    }
  }

  fun uploadImage(bitmap: Bitmap, name: String) {
    imageMap[name] =
        Face(
            contentDescription = name,
            data = bitmap.asImageBitmap()) // TODO make sure upload was successful
    firebase.uploadBitmap(name, ImageSetDTO(image = bitmap, contentDescription = name))
  }

  fun saveUser() {
    if (!userConfigIsNull && dices.isNotEmpty()) {
      firebase.uploadUserConfig(
          USER, UserDTO(dices = dices.map { it.key }, diceGroups = diceGroups))
    }
  }

  // Firebase Access end

}
