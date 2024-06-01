package com.example.dynamicdiceprototype.services

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamicdiceprototype.DTO.ImageDTO
import com.example.dynamicdiceprototype.DTO.UserDTO
import com.example.dynamicdiceprototype.DTO.toDice
import com.example.dynamicdiceprototype.Exceptions.DiceGroupNotFoundException
import com.example.dynamicdiceprototype.Exceptions.PermittedActionException
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.DiceGroup
import com.example.dynamicdiceprototype.data.DiceState
import com.example.dynamicdiceprototype.data.Face
import com.example.dynamicdiceprototype.data.toDiceDTO
import kotlin.random.Random
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

// extend ViewModel to survive configuration change (landscape mode)
object DiceViewModel : ViewModel() {
  val firebase = FirebaseDataStore()
  var currentDices by mutableStateOf(listOf<Dice>())
  var imageMap by mutableStateOf(mapOf<String, ImageDTO>())
  var collectFlows by mutableStateOf(0)

  // create Dice
  var diceInEdit by mutableStateOf<Dice>(Dice()) // TODO make nullable

  // create Dice Group
  var groupInEdit by mutableStateOf<Pair<String, DiceGroup>?>(null)
  var isGroupEditMode by mutableStateOf<Boolean>(false)

  // User Config
  val diceGroups = mutableStateMapOf<String, DiceGroup>()
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

  fun setDiceName(name: String) {
    diceInEdit.name = name
  }

  fun createNewDice() {
    diceInEdit = Dice(name = "Change Later")
  }

  fun setSelectedFaces(values: Map<ImageDTO, Int>) {
    diceInEdit.faces =
        values.map { entry ->
          Face(
              data = FirebaseDataStore.base64ToBitmap(entry.key.base64String),
              weight = entry.value,
              contentDescription =
                  entry.key.contentDescription) // TODO mapper from Face -> ImageDTO and vice versa
        }
  }

  fun setColor(color: Color) {
    diceInEdit.backgroundColor = color
  }

  private fun addDice(dice: Dice) {
    dices[dice.id] = dice
    firebase.uploadDice(dice.id, dice.toDiceDTO())
    saveUser()
  }

  fun saveDice() {
    addDice(
        diceInEdit) // TODO consider using events to set and update local data instead of doing it
    // locally and with firebase to avoid data inconsistencies

  }

  // end create dice

  // Dice Menu Actions

  fun removeKeyFromInnerMaps(diceGroups: MutableMap<String, DiceGroup>, keyToRemove: String) {
    diceGroups.keys.forEach { groupKey ->
      diceGroups[groupKey] =
          DiceGroup(
              dices = diceGroups[groupKey]?.dices?.filterKeys { it != keyToRemove } ?: emptyMap(),
              states = diceGroups[groupKey]?.states ?: emptyList())
    }
  }

  val nonMutableDices = listOf("6er")

  fun removeDice(dice: Dice) {
    if (nonMutableDices.contains(dice.name))
        throw PermittedActionException("Can not make changes to Dice: ${dice.name}")
    dices.remove(dice.id)
    removeKeyFromInnerMaps(diceGroups, dice.id)
    firebase.deleteDice(dice.id)
    saveUser()
  }

  fun editDice(dice: Dice) {
    if (nonMutableDices.contains(dice.name))
        throw PermittedActionException("Can not make changes to Dice: ${dice.name}")
    diceInEdit = dice
  }

  fun duplicateDice(it: Dice) {
    val newDice = it.copy(id = "", name = it.name.plus("_copy"))
    addDice(newDice)
  }

  // Dice Menu Actions end

  fun selectDice(dice: Dice) {
    currentDices = listOf(dice)
  }

  // Dice end

  // Dice Group
  // create Dice Group
  fun setGroupInEditDices(name: String, dices: Map<Dice, Int>) {
    groupInEdit =
        Pair(name, DiceGroup(mapOf(*dices.map { Pair(it.key.id, it.value) }.toTypedArray())))
  }

  fun setSelectedStates(states: Map<ImageDTO, Int>) {
    groupInEdit =
        groupInEdit?.let { group ->
          Pair(group.first, group.second.copy(states = states.keys.map { it.contentDescription }))
        }
  }

  fun saveGroupInEdit() {
    groupInEdit?.let { diceGroups[it.first] = it.second }
    saveUser()
  }

  fun copyDiceGroup(name: String): Pair<String, DiceGroup> {
    val state = diceGroups[name] ?: throw DiceGroupNotFoundException("Group not found: $name")
    return copyDiceGroupIfNotExists(name.plus("_copy"), state)
  }

  fun copyDiceGroupIfNotExists(newName: String, state: DiceGroup): Pair<String, DiceGroup> {
    val uniqueName = generateUniqueName(newName, diceGroups.keys.toList())
    return Pair(uniqueName, state)
  }

  // create Dice Group end
  // group Menu Actions
  fun removeGroup(it: String) {
    diceGroups.remove(it)
    saveUser()
  }

  fun setGroupInEdit(groupId: String) {
    isGroupEditMode = true
    val group = diceGroups[groupId]
    group?.let { group -> groupInEdit = Pair(groupId, group.copy(states = group.states.toList())) }
  }

  fun duplicateGroup(gorupId: String) {
    val newDiceGroup = copyDiceGroup(gorupId)
    diceGroups[newDiceGroup.first] = newDiceGroup.second
  }

  // group Menu Actions end
  fun selectDiceGroup(groupId: String) {
    lastDiceGroup = groupId
    val newDicesState = mutableListOf<Dice>()
    diceGroups[groupId]?.dices?.forEach { (diceId, count) ->
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

  fun duplicateToCurrentDices(it: Dice) {
    val mutableCurrentDices = currentDices.toMutableList()
    mutableCurrentDices.add(it.copy(id = ""))
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
        val diceTasks =
            userDTO.dices.map {
              dices[it] = Dice(id = it)
              async { loadDice(it) }
            }
        val statesTask =
            userDTO.diceGroups.values.flatMap {
              it.states.map { async { loadImage(diceId = null, imageId = it) } }
            }

        statesTask.awaitAll()
        diceTasks.awaitAll()
        collectFlows++
        collectFlows++ // TODO resolve this weird double ++ colelction behaviour, probably use some
        // event subscription
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

  private suspend fun loadImage(diceId: String?, imageId: String) {
    val image = firebase.getImageFromId(imageId)
    val imageMapMutable = imageMap.toMutableMap()
    image?.let { imageNotNull ->
      imageMapMutable[imageId] = imageNotNull
      imageMap = imageMapMutable
      diceId?.let {
        val bitmap = FirebaseDataStore.base64ToBitmap(imageNotNull.base64String)
        val diceToUpdate = dices[diceId]
        diceToUpdate?.let { dice ->
          dices[diceId] =
              dice.copy(
                  faces =
                      dice.faces.map {
                        if (it.contentDescription == image.contentDescription)
                            it.copy(data = bitmap)
                        else it
                      })
        }
      }
    }
  }

  fun loadAllImages() {
    viewModelScope.launch {
      val images = firebase.loadAllImages()
      imageMap = images
    }
  }

  fun uploadImage(imageDTO: ImageDTO) {
    val newImageMap = imageMap.toMutableMap()
    newImageMap[imageDTO.contentDescription] = imageDTO // TODO make sure upload was successful
    imageMap = newImageMap
    firebase.uploadImageDTO(imageDTO)
  }

  fun uploadImages(images: List<ImageDTO>) {
    val newImageMap = imageMap.toMutableMap()
    images.forEach { newImageMap[it.contentDescription] = it }
    imageMap = newImageMap
    firebase.uploadImageDTOs(images)
  }

  fun saveUser() {
    if (!userConfigIsNull && dices.isNotEmpty()) {
      firebase.uploadUserConfig(
          USER, UserDTO(dices = dices.map { it.key }, diceGroups = diceGroups))
    }
  }

  // Firebase Access end

}
