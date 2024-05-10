package com.example.dynamicdiceprototype.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dynamicdiceprototype.R
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.DiceState
import com.example.dynamicdiceprototype.data.Face
import com.example.dynamicdiceprototype.data.ImageModel
import com.example.dynamicdiceprototype.services.getFaces
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

@Composable
fun ImageBitmap(image: ImageModel, modifier: Modifier = Modifier) {
  Image(bitmap = image.imageBitmap, contentDescription = image.contentDescription, modifier)
}

@Composable
fun DiceView(
    dice: Dice,
    size: Dp,
    modifier: Modifier = Modifier,
    onDiceClick: (dice: Dice) -> Unit = {},
    showWeight: Boolean = false,
) {
  val spacing = size.div(10)

  Box(contentAlignment = Alignment.Center, modifier = modifier.size(size)) {
    Box(
        modifier =
            Modifier.graphicsLayer {
                  rotationZ = dice.rotation
                  val scale = 1 / 1.4f // rotating the button increases width and height
                  scaleX = scale
                  scaleY = scale
                }
                .shadow(spacing, RoundedCornerShape(spacing))
                .clickable { onDiceClick(dice) }) {
          FaceView(
              face = dice.current,
              showWeight = showWeight,
              spacing = spacing.coerceAtMost(24.dp),
              color = dice.backgroundColor)
        }
    if (dice.state == DiceState.LOCKED) {
      LockIcon(modifier = Modifier.align(Alignment.TopEnd).size(spacing.times(2)))
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
fun FaceView(
    face: Face?,
    spacing: Dp,
    modifier: Modifier = Modifier,
    color: Color = Color.Transparent,
    showWeight: Boolean = true
) {
  val spacingMax = spacing.coerceAtMost(24.dp) // TODO refactor or not?

  val image = face?.data

  Box(modifier = modifier.aspectRatio(1f).background(color, RoundedCornerShape(spacingMax))) {
    SizedImage(
        image = image,
        Modifier.fillMaxSize().padding(spacingMax).clip(RoundedCornerShape(spacingMax)))
    if (showWeight && face != null && face.weight > 1) {
      NumberCircle(
          face.weight.toString(),
          fontSize = spacingMax.value.sp,
          modifier = Modifier.align(Alignment.TopEnd))
    }
  }
}

@Composable
private fun SizedImage(image: ImageModel?, modifier: Modifier = Modifier) {
  image?.let { ImageBitmap(image = image, modifier) }
      ?: ArrangedColumn (verticalArrangement = Arrangement.Center) {
        Image(
            painter = painterResource(id = R.drawable.rukaiya),
            contentDescription = "no Image",
            Modifier.size(10.dp))
        Text(text = "Throw Dice", fontSize = 20.sp)
      }
}

@Preview
@Composable
private fun Preview() {

  DynamicDicePrototypeTheme {
    //    FaceView(face = Face(weight = 20), showWeight = true, spacing = 36.dp, color = Color.Cyan)
    DiceView(dice = Dice(faces = getFaces(2)), onDiceClick = {}, size = 300.dp)
  }
}

@Composable
fun LockIcon(modifier: Modifier = Modifier) {
  Icon(imageVector = Icons.Filled.Lock, contentDescription = "LOCKED", modifier = modifier)
}
