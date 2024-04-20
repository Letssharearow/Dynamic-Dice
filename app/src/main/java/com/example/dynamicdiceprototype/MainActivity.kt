package com.example.dynamicdiceprototype

import LandingPage
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val firebaseDataStore = FirebaseDataStore()
    //    val res = resources
    //    val ids =
    //        arrayOf(
    //            R.drawable.two_transparent,
    //            R.drawable.three_transparent,
    //            R.drawable.four_transparent,
    //            R.drawable.five_transparent,
    //            R.drawable.six_transparent,
    //        )
    //    ids.forEach {
    //      var bitmap = BitmapFactory.decodeResource(res, it)
    //      firebaseDataStore.uploadBitmap("$it", bitmap)
    //    }
    firebaseDataStore.fetchAndStoreCollection()

    setContent {
      val imageBitmap = firebaseDataStore.image
      val images = firebaseDataStore.images
      Log.i("MyApp", "setContent images ${images.values}")
      DynamicDicePrototypeTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          if (imageBitmap != null) {
            ImageFromBase64(imageBitmap = imageBitmap)
          }
          Column { images.values.forEach { ImageFromBase64(imageBitmap = it) } }
          //          MyApp()
        }
      }
    }
  }
}

@Composable
fun ImageFromBase64(imageBitmap: ImageBitmap) {
  Log.i("MyApp", "imageBitmap $imageBitmap")
  Image(bitmap = imageBitmap, contentDescription = null)
}

@Composable
fun MyApp() {
  val viewModel: DiceViewModel = viewModel<DiceViewModel>()
  val name = "Julis Dice Bundle"
  LandingPage(
      dices = viewModel.dicesState,
      name = name,
  )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  DynamicDicePrototypeTheme { MyApp() }
}
