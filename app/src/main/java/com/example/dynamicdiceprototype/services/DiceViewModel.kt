package com.example.dynamicdiceprototype.services

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dynamicdiceprototype.DTO.ImageDTO
import com.example.dynamicdiceprototype.DTO.UserDTO
import com.example.dynamicdiceprototype.DTO.toDice
import com.example.dynamicdiceprototype.Exceptions.PermittedActionException
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.DiceGroup
import com.example.dynamicdiceprototype.data.DiceLockState
import com.example.dynamicdiceprototype.data.Face
import com.example.dynamicdiceprototype.data.toDiceDTO
import com.example.dynamicdiceprototype.services.serializer.DiceDTOMap
import com.example.dynamicdiceprototype.services.serializer.ImageDTOMap
import com.example.dynamicdiceprototype.utils.temp_group_id
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

// extend ViewModel to survive configuration change (landscape mode)
class DiceViewModel(
    imageDataStore: DataStore<ImageDTOMap>,
    diceDataStore: DataStore<DiceDTOMap>,
    userDataStore: DataStore<UserDTO>
) : ViewModel() {

  // dataStore
  val imagesStore = imageDataStore // TODO make private, updateData should not be public
  val dicesStore = diceDataStore
  val userConfigStore = userDataStore

  val firebase = FirebaseDataStore()
  var currentDices by mutableStateOf(listOf<Dice>())

  var imageMap by mutableStateOf<Map<String, ImageDTO>>(emptyMap())
  var hasLoadedUser by mutableStateOf(false)

  // create Dice
  var diceInEdit by mutableStateOf<Dice>(Dice()) // TODO make nullable
  var isDiceEditMode by mutableStateOf<Boolean>(false) //

  // create Dice Group
  var groupInEdit by mutableStateOf<DiceGroup?>(null)
  var isGroupEditMode by mutableStateOf<Boolean>(false)

  // User Config
  var diceGroups by mutableStateOf<Map<String, DiceGroup>>(emptyMap())
  var dices by mutableStateOf<Map<String, Dice>>(emptyMap())
  var lastDiceGroup by mutableStateOf("Red flag or Green flag")

  var toastMessageText by mutableStateOf<String?>(null)

  var selectedImages by mutableStateOf(emptyMap<ImageDTO, Int>())

  init {
    populatedDicesWithImages()
    collectUserConfig()
  }

  private fun collectUserConfig() {
    viewModelScope.launch {
      userConfigStore.data.collect {
        Log.d(TAG, "ViewModel collectUserConfig flow: groups key ${it.diceGroups.keys}")
        diceGroups = it.diceGroups
        hasLoadedUser = true
        if (!it.diceGroups.keys.contains(temp_group_id)) {
          saveGroup(DiceGroup(id = temp_group_id))
        }
      }
    }
  }

  private fun generateUniqueName(baseName: String, keys: List<String>): String {
    var uniqueName = baseName
    while (keys.contains(uniqueName)) {
      uniqueName += "_copy"
    }
    return uniqueName
  }

  // Dice

  // create Dice Flow

  fun setDiceName(name: String) {
    diceInEdit.name = name
  }

  fun createNewDice() {
    diceInEdit = Dice(name = "Change Later")
    isDiceEditMode = false
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
    viewModelScope.launch {
      dicesStore.updateData {
        val toMutableMap = it.dices.toMutableMap()
        toMutableMap[dice.id] = dice.toDiceDTO()
        it.copy(dices = toMutableMap)
      }
    }
  }

  private fun populatedDicesWithImages() {
    viewModelScope.launch {
      combine(
              dicesStore.data,
              imagesStore.data,
          ) { dices, images ->
            // Process the data from both DataStores here
            dices to images // Pair the data for easier access
          }
          .collect { (dicesFlow, imagesFlow) ->
            Log.d(
                TAG,
                "ViewModel populatedDicesWithImages flow: dices keys ${dicesFlow.dices.keys} images Size: ${imagesFlow.images.keys.size}")
            dices =
                dicesFlow.dices.mapValues { diceDTOEntry ->
                  diceDTOEntry.value.toDice(diceDTOEntry.key, imagesFlow.images)
                }
            imageMap = imagesFlow.images.mapValues { it.value.copy(contentDescription = it.key) }
          }
    }
  }

  fun saveDice() {
    addDice(if (isDiceEditMode) diceInEdit else diceInEdit.copy(id = ""))
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
    viewModelScope.launch {
      dicesStore.updateData {
        val toMutableMap = it.dices.toMutableMap()
        toMutableMap.remove(dice.id)
        it.copy(dices = toMutableMap)
      }
    }
    viewModelScope.launch {
      userConfigStore.updateData {
        val toMutableMap = it.diceGroups.toMutableMap()
        removeKeyFromInnerMaps(toMutableMap, dice.id)
        it.copy(diceGroups = toMutableMap)
      }
    }
  }

  fun editDice(dice: Dice) {
    if (nonMutableDices.contains(dice.name))
        throw PermittedActionException("Can not make changes to Dice: ${dice.name}")
    isDiceEditMode = true
    diceInEdit = dice
  }

  fun duplicateDice(it: Dice) {
    val newDice = it.copy(id = "", name = it.name.plus("_copy"))
    addDice(newDice)
  }

  // Dice Menu Actions end

  fun selectDice(dice: Dice) {
    diceGroups[temp_group_id]?.let { saveGroup(it.copy(dices = mapOf(dice.id to 1))) }
  }

  // Dice end

  // Dice Group
  // create Dice Group
  fun createNewGroup() {
    groupInEdit = DiceGroup()
    isGroupEditMode = false
  }

  fun setGroupInEditDices(name: String, dices: Map<Dice, Int>) {
    groupInEdit =
        groupInEdit?.copy(
            name = name, dices = mapOf(*dices.map { Pair(it.key.id, it.value) }.toTypedArray()))
            ?: DiceGroup(
                name = name,
                dices = mapOf(*dices.map { Pair(it.key.id, it.value) }.toTypedArray()),
            )
  }

  fun setSelectedStates(states: Map<ImageDTO, Int>) {
    groupInEdit =
        groupInEdit?.let { group -> group.copy(states = states.keys.map { it.contentDescription }) }
  }

  fun saveGroupInEdit() {
    groupInEdit?.let { saveGroup(it) }
  }

  fun saveGroup(group: DiceGroup) {
    viewModelScope.launch {
      userConfigStore.updateData { userDTO ->
        val toMutableMap = userDTO.diceGroups.toMutableMap()
        group.let { group -> toMutableMap[group.id] = group }
        userDTO.copy(diceGroups = toMutableMap.mapKeys { it.value.id })
      }
    }
  }

  // create Dice Group end
  // group Menu Actions
  fun removeGroup(groupId: String) {
    if (groupId == temp_group_id) {
      return
    }
    viewModelScope.launch {
      userConfigStore.updateData {
        val toMutableMap = it.diceGroups.toMutableMap()
        toMutableMap.remove(groupId)
        it.copy(diceGroups = toMutableMap)
      }
    }
  }

  fun setGroupInEdit(groupId: String) {
    isGroupEditMode = true
    val group = diceGroups[groupId]
    group?.let { group -> groupInEdit = group.copy(dices = group.dices.toMap()) }
  }

  fun duplicateGroup(groupId: String) {
    val newDiceGroup =
        diceGroups[groupId]?.copy(
            id = "",
            name =
                generateUniqueName(
                    baseName = diceGroups[groupId]?.name ?: "", diceGroups.values.map { it.name }))
            ?: DiceGroup()
    viewModelScope.launch {
      userConfigStore.updateData {
        val toMutableMap = it.diceGroups.toMutableMap()
        toMutableMap[newDiceGroup.id] = newDiceGroup
        it.copy(diceGroups = toMutableMap)
      }
    }
  }

  // group Menu Actions end

  fun saveDiceGroup(group: DiceGroup) {
    val tempGroup = diceGroups[temp_group_id]!!
    saveGroup(group.copy(id = tempGroup.id, name = tempGroup.name))
  }

  fun selectDiceGroup(groupId: String) {
    diceGroups[groupId]?.dices?.let { diceMap ->
      setNewCurrentDices(diceMap.mapKeys { dices[it.key]!! })
    }
  }

  // Dice Group end

  private fun addToCurrentDices(oldState: List<Dice>, dices: Map<Dice, Int>) {
    val newDicesState = oldState.toMutableList()
    dices.forEach { (dice, count) ->
      for (i in 1..count) {
        newDicesState.add(dice.copy(rotation = 0f))
      }
    }
    currentDices = newDicesState
  }

  // Main Screen actions
  fun setNewCurrentDices(dices: Map<Dice, Int>) {
    addToCurrentDices(emptyList(), dices)
  }

  fun duplicateToCurrentDices(newDices: Map<Dice, Int>) {
    addToCurrentDices(currentDices, newDices)
  }

  fun rollSingleDice(dice: Dice) {
    currentDices =
        currentDices.map {
          if (it === dice) {
            it.roll()
          } else it
        }
  }

  fun lockDice(dice: Dice) {
    currentDices =
        // use Map function to trigger recomposition
        currentDices.map {
          if (it ===
              dice) { // use === to compare references so that dices with same rotation and Id still
            // are different
            // use copy function to trigger recomposition
            if (dice.diceLockState == DiceLockState.UNLOCKED)
                dice.copy(diceLockState = DiceLockState.LOCKED, rotation = 0f)
            else {
              dice.copy(diceLockState = DiceLockState.UNLOCKED, rotation = 0f)
            }
          } else it
        }
  }

  fun rollDices() {
    currentDices =
        currentDices.map { dice ->
          if (dice.diceLockState != DiceLockState.LOCKED) {
            dice.roll()
          } else {
            dice
          }
        }
  }

  fun resetCurrentDices() {
    currentDices = currentDices.map { it.reset() }
  }

  fun setCurrentDicesState(state: DiceLockState) {
    currentDices = currentDices.map { it.copy(diceLockState = state) }
  }

  fun saveCurrentDices() {
    val newDices = mutableMapOf<String, Int>()
    currentDices.forEach { newDices[it.id] = newDices[it.id]?.plus(1) ?: 1 }
    diceGroups[temp_group_id]?.let { saveGroup(it.copy(dices = newDices)) }
  }

  // Main Screen actions end

  // Firebase Access
  fun getErrorMessage() = firebase.errorMessage

  fun saveImages(newImages: List<ImageDTO>) {
    viewModelScope.launch {
      imagesStore.updateData { t ->
        val mutableMapState = t.images.toMutableMap()
        newImages.forEach { mutableMapState[it.contentDescription] = it }
        t.copy(images = mutableMapState) // TODO make persistentMap()?
      }
    }
  }

  // Firebase Access end
  fun changeSelectedImages(images: Map<ImageDTO, Int>) {
    selectedImages = images
  }

  fun deleteImages(images: Map<ImageDTO, Int>) {
    viewModelScope.launch {
      imagesStore.updateData { t ->
        val mutableMapState = t.images.toMutableMap()
        images.forEach { mutableMapState.remove(it.key.contentDescription) }
        t.copy(images = mutableMapState) // TODO make persistentMap()?
      }
    }
  }
}

class DiceViewModelFactory(
    private val imageDataStore: DataStore<ImageDTOMap>,
    private val diceDataStore: DataStore<DiceDTOMap>,
    private val userDataStore: DataStore<UserDTO>
) : ViewModelProvider.Factory {

  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(DiceViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return DiceViewModel(imageDataStore, diceDataStore, userDataStore) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
