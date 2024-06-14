package com.example.dynamicdiceprototype.services

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
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
import com.example.dynamicdiceprototype.services.serializer.DiceDTOMap
import com.example.dynamicdiceprototype.services.serializer.ImageDTOMap
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

// extend ViewModel to survive configuration change (landscape mode)
class DiceViewModel(
    imageDataStore: DataStore<ImageDTOMap>,
    diceDataStore: DataStore<DiceDTOMap>,
    userDataStore: DataStore<UserDTO>
) : ViewModel() {

  // dataStore
  val imagesStore = imageDataStore.data
  val dicesStore = diceDataStore.data
  val userConfigStore = userDataStore.data

  val firebase = FirebaseDataStore()
  var currentDices by mutableStateOf(listOf<Dice>())
  var imageMap = mutableStateMapOf<String, ImageDTO>()
  var hasLoadedUser by mutableStateOf(false)

  // create Dice
  var diceInEdit by mutableStateOf<Dice>(Dice()) // TODO make nullable
  var isDiceEditMode by mutableStateOf<Boolean>(false) //

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
    firebase.uploadDice(
        dice.id,
        dice.toDiceDTO(),
        onSuccess = {
          dices[it] = dice
          saveUser()
        })
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
    dices.remove(dice.id)
    removeKeyFromInnerMaps(diceGroups, dice.id)
    firebase.deleteDice(dice.id)
    saveUser()
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
    group?.let { group -> groupInEdit = Pair(groupId, group.copy(dices = group.dices.toMap())) }
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

  private fun loadUserConfig() {
    viewModelScope.launch {
      val userDTO = firebase.fetchUserData(USER_FETCH)
      hasLoadedUser = true
      if (userDTO != null) {
        userDTO.diceGroups.forEach { diceGroups[it.key] = it.value } // TODO handle User null
        val statesTask =
            userDTO.diceGroups.values.flatMap {
              it.states.map { async { loadImage(diceId = null, imageId = it) } }
            }
        val diceTasks =
            userDTO.dices.map {
              dices[it] = Dice(id = it)
              async { loadDice(it) }
            }
        statesTask.awaitAll()
        diceTasks.awaitAll()
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
    image?.let { imageNotNull ->
      imageMap[imageId] = imageNotNull
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
      images.forEach { imageMap[it.key] = it.value }
    }
  }

  fun uploadImage(imageDTO: ImageDTO) {
    firebase.uploadImageDTO(
        image = imageDTO, onSuccess = { imageId -> imageMap[imageId] = imageDTO })
  }

  fun uploadImages(images: List<ImageDTO>) {
    for (image in images) {
      firebase.uploadImageDTO(image = image, onSuccess = { imageId -> imageMap[imageId] = image })
    }
  }

  fun saveUser() {
    if (!userConfigIsNull && dices.isNotEmpty()) {
      firebase.uploadUserConfig(
          USER,
          UserDTO(dices = dices.map { it.key }, diceGroups = diceGroups),
          onSuccess = {}) // TODO consider also saving userData to firebase first and saving it
      // locally when the call was successful
    }
  }

  // Firebase Access end

}
