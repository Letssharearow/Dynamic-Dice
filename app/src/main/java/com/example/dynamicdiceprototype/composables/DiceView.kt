package com.example.dynamicdiceprototype.composables

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dynamicdiceprototype.R
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.DiceState
import com.example.dynamicdiceprototype.data.Face
import com.example.dynamicdiceprototype.data.ImageModel
import com.example.dynamicdiceprototype.services.DiceViewModel
import com.example.dynamicdiceprototype.services.TAG

@Composable
fun ImageBitmap(image: ImageModel, modifier: Modifier = Modifier) {
  Image(bitmap = image.imageBitmap, contentDescription = image.contentDescription, modifier)
}

@Composable
fun DiceView(dice: Dice, size: Dp, modifier: Modifier = Modifier) {
  val viewModel: DiceViewModel = viewModel<DiceViewModel>()

  Log.d(TAG, "Recompose DiceView dice => $dice")
  val image = dice.current?.data

  Box(contentAlignment = Alignment.Center, modifier = modifier.size(size = size)) {
    Button(
        onClick = { viewModel.lockDice(dice) },
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(0.dp),
        modifier =
            Modifier.aspectRatio(1F)
                .graphicsLayer {
                  rotationZ = dice.rotation
                  val scale =
                      1 / 1.4F // rotating the button increases width and height 1.4 is the length
                  scaleX = scale
                  scaleY = scale
                }
                .shadow(8.dp, RoundedCornerShape(20.dp))) {
          if (image != null) {
            ImageBitmap(image = image, modifier = Modifier.fillMaxSize().padding(16.dp))
          } else {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription =
                    dice.current?.imageId ?: "no Image found") // TODO String reference
          }
        }
    if (dice.state == DiceState.LOCKED) {
      LockIcon(modifier = Modifier.align(Alignment.TopEnd).size(36.dp))
    }
  }
}

@Composable
fun FaceView(face: Face, size: Dp, modifier: Modifier = Modifier) {
  Log.d(TAG, "Recompose DiceView dice => $face")
  val image = face.data

  Box(contentAlignment = Alignment.Center, modifier = modifier.size(size = size)) {
    if (image != null) {
      ImageBitmap(image = image, modifier = Modifier.fillMaxSize().padding(size.div(10)))
    } else {
      Image(
          painter = painterResource(id = R.drawable.ic_launcher_background),
          contentDescription = "no Image",
          modifier = Modifier.fillMaxSize().padding(size.div(10))) // TODO String reference
    }
  }
}

@Composable
fun LockIcon(modifier: Modifier = Modifier) {
  Icon(imageVector = Icons.Filled.Lock, contentDescription = "LOCKED", modifier = modifier)
}
