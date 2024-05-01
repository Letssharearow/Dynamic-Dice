package com.example.dynamicdiceprototype.composables

import android.content.Context
import android.util.Log
import android.util.TypedValue
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dynamicdiceprototype.R
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.DiceState
import com.example.dynamicdiceprototype.data.Face
import com.example.dynamicdiceprototype.data.ImageModel
import com.example.dynamicdiceprototype.services.DiceViewModel
import com.example.dynamicdiceprototype.services.TAG
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

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
fun NumberCircle(text: String, modifier: Modifier = Modifier, fontSize: TextUnit = 12.sp) {
  Box(
      modifier =
          modifier
              .wrapContentSize()
              .padding(8.dp)
              .background(Color(0x80000000), CircleShape)
              .border(BorderStroke(2.dp, MaterialTheme.colorScheme.secondary), CircleShape)) {
        Text(
            text = text,
            fontSize = fontSize,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center).wrapContentSize().padding(8.dp))
      }
}

@Composable
fun FaceView(face: Face?, size: Dp?, modifier: Modifier = Modifier, showWeight: Boolean = true) {
  Log.d(TAG, "Recompose DiceView dice => $face")
  val image = face?.data
  val sizeFallback = size ?: 24.dp

  val modifier1 = if (size != null) modifier.size(size = size) else modifier.fillMaxSize()
  Box(contentAlignment = Alignment.Center, modifier = modifier1) {
    if (image != null) {
      ImageBitmap(image = image, modifier = Modifier.fillMaxSize().padding(sizeFallback.div(10)))
    } else {
      Image(
          painter = painterResource(id = R.drawable.ic_launcher_background),
          contentDescription = "no Image",
          modifier = Modifier.fillMaxSize().padding(sizeFallback.div(10))) // TODO String reference
    }
    if (showWeight && face != null && face.weight > 1) {
      NumberCircle(
          face.weight.toString(),
          fontSize = Math.min(sizeFallback.value, 36F * 6).sp.div(6),
          modifier = Modifier.align(Alignment.TopEnd))
    }
  }
}

fun Number.spToPx(context: Context) =
    TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, this.toFloat(), context.resources.displayMetrics)
        .toInt()

@Preview
@Composable
private fun Preview() {

  DynamicDicePrototypeTheme { FaceView(face = Face(weight = 20), size = 100.dp, showWeight = true) }
}

@Composable
fun LockIcon(modifier: Modifier = Modifier) {
  Icon(imageVector = Icons.Filled.Lock, contentDescription = "LOCKED", modifier = modifier)
}
