package com.example.dynamicdiceprototype.composables.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.dynamicdiceprototype.DTO.ImageDTO
import com.example.dynamicdiceprototype.composables.SingleLineInput
import com.example.dynamicdiceprototype.services.FirebaseDataStore
import java.io.InputStream

@Composable
fun UploadImageScreen(
    context: Context,
    onImageSelected: (ImageDTO) -> Unit = {}, // TODO add toast
) {
  val (imageName, setImageName) = remember { mutableStateOf("") }
  val (bitmap, setBitmap) = remember { mutableStateOf<Bitmap?>(null) }
  val (tags, setTags) = remember { mutableStateOf(listOf<String>()) }
  val (newTag, setNewTag) = remember { mutableStateOf("") }

  val imagePickerLauncher =
      rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
          val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
          val originalBitmap = BitmapFactory.decodeStream(inputStream)
          val reducedBitmap = reduceImageSize(originalBitmap, 200, 200)
          setBitmap(reducedBitmap)
        }
      }

  Column(
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxSize()) {
        if (bitmap != null) {
          ImageNameInput(imageName, setImageName)
          if (imageName.isNotEmpty()) {
            TagInput(newTag, setNewTag, tags, setTags)
          }
          Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier =
                    Modifier.size(200.dp)
                        .clickable { imagePickerLauncher.launch("image/*") }
                        .padding(16.dp))
            DisplayTags(tags, setTags)
          }
          UploadButton(
              imageName,
              bitmap,
              tags,
              onImageSelected,
              {
                setBitmap(null) // Reset the bitmap to null after successful upload
                setImageName("") // Clear the image name
                setTags(emptyList()) // Clear the tags
              },
          )
        } else {
          Button(
              onClick = { imagePickerLauncher.launch("image/*") },
          ) {
            Text("+")
          }
        }
      }
}

@Composable
fun ImageNameInput(imageName: String, setImageName: (String) -> Unit) {
  SingleLineInput(
      text = imageName,
      onValueChange = setImageName,
      label = "Image Name",
      Modifier.padding(8.dp),
      isError = imageName.isEmpty())
}

@Composable
fun TagInput(
    newTag: String,
    setNewTag: (String) -> Unit,
    tags: List<String>,
    setTags: (List<String>) -> Unit
) {
  Row(Modifier.fillMaxWidth().padding(8.dp)) {
    OutlinedTextField(
        value = newTag,
        onValueChange = setNewTag,
        label = { Text("New Tag") },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions =
            KeyboardActions(
                onDone = {
                  if (newTag.isNotEmpty() && tags.size < 3) {
                    setTags(tags + newTag)
                    setNewTag("")
                  }
                }),
        modifier = Modifier.weight(1f))
  }
}

@Composable
fun DisplayTags(tags: List<String>, setTags: (List<String>) -> Unit) {
  Column(Modifier.padding(horizontal = 8.dp)) {
    tags.forEach { tag ->
      Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier =
              Modifier.fillMaxWidth()
                  .padding(vertical = 4.dp)
                  .background(MaterialTheme.colorScheme.secondaryContainer)
                  .padding(4.dp)) {
            Text(tag)
            IconButton(onClick = { setTags(tags.filter { it != tag }) }) {
              Icon(Icons.Default.Delete, contentDescription = "Delete Tag")
            }
          }
    }
  }
}

@Composable
fun ImagePickerBox(
    bitmap: Bitmap?,
    imagePickerLauncher: ManagedActivityResultLauncher<String, Uri?>,
    modifier: Modifier = Modifier
) {
  Box(contentAlignment = Alignment.Center, modifier = modifier) {
    bitmap?.let {
      Image(
          bitmap = it.asImageBitmap(),
          contentDescription = null,
          modifier =
              Modifier.size(200.dp)
                  .clickable { imagePickerLauncher.launch("image/*") }
                  .padding(16.dp))
    } ?: Button(onClick = { imagePickerLauncher.launch("image/*") }) { Text("+") }
  }
}

@Composable
fun UploadButton(
    imageName: String,
    bitmap: Bitmap?,
    tags: List<String>,
    onImageSelected: (ImageDTO) -> Unit,
    onUploadSuccess: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Button(
      onClick = {
        if (imageName.isNotEmpty() && bitmap != null) {
          onImageSelected(
              ImageDTO(
                  contentDescription = imageName,
                  base64String = FirebaseDataStore.bitmapToBase64(bitmap),
                  tags = tags))
          onUploadSuccess()
        }
      },
      enabled = imageName.isNotEmpty(),
      modifier = modifier.padding(vertical = 8.dp)) {
        Text("Upload Image")
      }
}

@Composable
fun DisplayUploadSuccess(uploadSuccess: Boolean) {
  if (uploadSuccess) {
    Text("Image uploaded successfully!", color = Color.Green)
  }
}

fun reduceImageSize(bitmap: Bitmap, desiredWidth: Int, desiredHeight: Int): Bitmap {
  val options =
      BitmapFactory.Options().apply {
        inJustDecodeBounds = true
        // Calculate inSampleSize
        inSampleSize = calculateInSampleSize(this, desiredWidth, desiredHeight)
        inJustDecodeBounds = false
      }
  // Use inSampleSize to decode bitmap again
  return Bitmap.createScaledBitmap(bitmap, desiredWidth, desiredHeight, true)
}

fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
  val (height: Int, width: Int) = options.run { outHeight to outWidth }
  var inSampleSize = 1

  if (height > reqHeight || width > reqWidth) {
    val halfHeight: Int = height / 2
    val halfWidth: Int = width / 2

    while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
      inSampleSize *= 2
    }
  }
  return inSampleSize
}
