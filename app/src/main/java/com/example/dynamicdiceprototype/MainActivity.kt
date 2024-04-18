package com.example.dynamicdiceprototype

import LandingPage
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme
import kotlin.io.encoding.Base64

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val firebaseDataStore = FirebaseDataStore()
    val res = resources
    val id = R.drawable.two_transparent
    val bitmap = BitmapFactory.decodeResource(res, id)
    //    firebaseDataStore.uploadBitmap("two_transparent", bitmap)
    firebaseDataStore.getBitMapFromDataStore()

    setContent {
      val base64String = firebaseDataStore.image
      Log.i("MyApp", "base64String $base64String")
      DynamicDicePrototypeTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          ImageFromBase64(base64String = base64String)
          MyApp()
        }
      }
    }
  }
}

@OptIn(kotlin.io.encoding.ExperimentalEncodingApi::class)
fun base64ToBitmap(base64String: String): Bitmap {
  val decodedBytes = Base64.decode(base64String)
  return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
}

@Composable
fun ImageFromBase64(base64String: String) {
  if (base64String.isEmpty()) {
    return Text(text = "empty string") // TODO better handling
  }
  Log.i("MyApp", "ImageFromBase64 base64String $base64String")
  val bitmap = base64ToBitmap(base64String)
  Log.i("MyApp", "bitmap $bitmap")
  val imageBitmap = bitmap.asImageBitmap()
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
