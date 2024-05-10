package com.example.dynamicdiceprototype.composables.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import java.io.InputStream

@Composable
fun UploadImageScreen(context: Context, onImageSelected: (Bitmap) -> Unit) {
  val imagePickerLauncher =
      rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
          val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
          val originalBitmap = BitmapFactory.decodeStream(inputStream)
          val reducedBitmap = reduceImageSize(originalBitmap, 200, 200) // Example size
          onImageSelected(reducedBitmap)
        }
      }

  Button(onClick = { imagePickerLauncher.launch("image/*") }) { Text("Upload Image") }
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
