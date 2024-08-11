package com.example.dynamicdiceprototype.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint

class ImageCreator {

  fun createSquareImage(context: Context, fileName: String, size: Int, color: Int) {
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint()

    paint.color = color
    canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), paint)

    // Use openFileOutput to get a FileOutputStream for the internal storage directory
    context.openFileOutput(fileName, Context.MODE_PRIVATE).use { outputStream ->
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    }
  }

  fun getBitmap(size: Int, color: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint()

    paint.color = color
    canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), paint)

    return bitmap
  }

  //  fun createSquareImage2(filePath: String, size: Int, color: Color) {
  //    val bufferedImage = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
  //    val graphics: Graphics2D = bufferedImage.createGraphics()
  //
  //    // Fill the image with the specified color
  //    graphics.color = color
  //    graphics.fillRect(0, 0, size, size)
  //
  //    // Save the image to the specified file path
  //    val outputFile = File(filePath)
  //    ImageIO.write(bufferedImage, "PNG", outputFile)
  //
  //    graphics.dispose()
  //  }
}

fun main() {
  val imageCreator = ImageCreator()
}
