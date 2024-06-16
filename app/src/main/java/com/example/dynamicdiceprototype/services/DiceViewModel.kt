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
import com.example.dynamicdiceprototype.Exceptions.DiceGroupNotFoundException
import com.example.dynamicdiceprototype.Exceptions.PermittedActionException
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.DiceGroup
import com.example.dynamicdiceprototype.data.DiceState
import com.example.dynamicdiceprototype.data.Face
import com.example.dynamicdiceprototype.data.toDiceDTO
import com.example.dynamicdiceprototype.services.serializer.DiceDTOMap
import com.example.dynamicdiceprototype.services.serializer.ImageDTOMap
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
  var groupInEdit by mutableStateOf<Pair<String, DiceGroup>?>(null)
  var isGroupEditMode by mutableStateOf<Boolean>(false)

  // User Config
  var diceGroups by mutableStateOf<Map<String, DiceGroup>>(emptyMap())
  var dices by mutableStateOf<Map<String, Dice>>(emptyMap())
  var lastDiceGroup by mutableStateOf("Red flag or Green flag")

  var toastMessageText by mutableStateOf<String?>(null)

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

  fun setDataStore() {
    viewModelScope.launch {
      dicesStore.updateData { DiceDTOMap(dices.mapValues { it.value.toDiceDTO() }) }
      userConfigStore.updateData { UserDTO(dices = dices.keys.toList(), diceGroups = diceGroups) }
    }
  }

  fun createNewGroup() {
    groupInEdit = Pair("Change Later", DiceGroup())
    isGroupEditMode = false
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
            imageMap = imagesFlow.images
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
    currentDices = listOf(dice)
  }

  // Dice end

  // Dice Group
  // create Dice Group
  fun setGroupInEditDices(name: String, dices: Map<Dice, Int>) {
    groupInEdit =
        Pair(
            name,
            DiceGroup(
                dices = mapOf(*dices.map { Pair(it.key.id, it.value) }.toTypedArray()),
                states = groupInEdit?.second?.states ?: emptyList()))
  }

  fun setSelectedStates(states: Map<ImageDTO, Int>) {
    groupInEdit =
        groupInEdit?.let { group ->
          Pair(group.first, group.second.copy(states = states.keys.map { it.contentDescription }))
        }
  }

  fun saveGroupInEdit() {
    viewModelScope.launch {
      userConfigStore.updateData {
        val toMutableMap = it.diceGroups.toMutableMap()
        groupInEdit?.let { group -> toMutableMap[group.first] = group.second }
        it.copy(diceGroups = toMutableMap)
      }
    }
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
  fun removeGroup(groupId: String) {
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
    group?.let { group -> groupInEdit = Pair(groupId, group.copy(dices = group.dices.toMap())) }
  }

  fun duplicateGroup(groupId: String) {
    val newDiceGroup = copyDiceGroup(groupId)
    viewModelScope.launch {
      userConfigStore.updateData {
        val toMutableMap = it.diceGroups.toMutableMap()
        toMutableMap[newDiceGroup.first] = newDiceGroup.second
        it.copy(diceGroups = toMutableMap)
      }
    }
  }

  // group Menu Actions end
  fun selectDiceGroup(groupId: String) {
    lastDiceGroup = groupId
    val newDicesState = mutableListOf<Dice>()
    diceGroups[groupId]?.dices?.forEach { (diceId, count) ->
      val diceToAdd = dices[diceId]
      diceToAdd?.let {
        for (i in 1..count) {
          newDicesState.add(diceToAdd.copy(id = "", rotation = 0f))
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
            if (dice.state == DiceState.UNLOCKED) dice.copy(state = DiceState.LOCKED, rotation = 0f)
            else {
              dice.copy(state = DiceState.UNLOCKED, rotation = 0f)
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

  fun setCurrentDicesState(state: DiceState) {
    currentDices = currentDices.map { it.copy(state = state) }
  }

  // Main Screen actions end

  // Firebase Access
  fun getErrorMessage() = firebase.errorMessage

  fun uploadImages(newImages: List<ImageDTO>) {
    viewModelScope.launch {
      imagesStore.updateData { t ->
        val mutableMapState = t.images.toMutableMap()
        newImages.forEach { mutableMapState[it.contentDescription] = it }
        t.copy(images = mutableMapState) // TODO make persistentMap()?
      }
    }
  }
  // Firebase Access end

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
