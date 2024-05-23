package com.example.dynamicdiceprototype.composables.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.example.dynamicdiceprototype.DTO.ImageDTO
import com.example.dynamicdiceprototype.composables.SingleLineInput
import com.example.dynamicdiceprototype.services.FirebaseDataStore
import java.io.InputStream

@Composable
fun UploadImageScreen(context: Context, onImageSelected: (ImageDTO) -> Unit) {
  var imageName by remember { mutableStateOf("Proof of Concept") }
  var bitmap by remember { mutableStateOf<Bitmap?>(null) }
  val imagePickerLauncher =
      rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
          val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
          val originalBitmap = BitmapFactory.decodeStream(inputStream)
          val reducedBitmap = reduceImageSize(originalBitmap, 200, 200)
          bitmap = reducedBitmap
        }
      }

  Column(
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxSize()) {
        SingleLineInput(
            text = imageName,
            onValueChange = { imageName = it },
            label = "Image Name",
            Modifier.padding(8.dp))
        Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(1f)) {
          bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = imageName,
                Modifier.clickable { imagePickerLauncher.launch("image/*") })
          } ?: Button(onClick = { imagePickerLauncher.launch("image/*") }) { Text("+") }
        }
        Button(
            onClick = {
              if (imageName.isNotEmpty()) {
                bitmap?.let {
                  onImageSelected(
                      ImageDTO(
                          contentDescription = imageName,
                          base64String = FirebaseDataStore.bitmapToBase64(it)))
                }
              }
            },
            Modifier.padding(vertical = 8.dp)) {
              Text("Upload Image")
            }
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
