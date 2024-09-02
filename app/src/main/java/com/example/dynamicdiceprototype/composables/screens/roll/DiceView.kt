package com.example.dynamicdiceprototype.composables.screens.roll

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dynamicdiceprototype.R
import com.example.dynamicdiceprototype.composables.common.ArrangedColumn
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.data.DiceLockState
import com.example.dynamicdiceprototype.data.Face
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme
import com.example.dynamicdiceprototype.utils.IMAGE_DTO_NUMBER_CONTENT_DESCRIPTION

@Composable
fun ImageBitmap(image: ImageBitmap, contentDescription: String, modifier: Modifier = Modifier) {
  Image(bitmap = image, contentDescription = contentDescription, modifier)
}

@Composable
fun DiceView(
    dice: Dice,
    size: Dp,
    modifier: Modifier = Modifier,
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
            }) {
          FaceView(
              face = dice.current,
              showWeight = showWeight,
              spacing = spacing.coerceAtMost(24.dp),
              size = size.div(3),
              color = dice.backgroundColor)
          NumberCircle(
              text = "${dice.faces.sumOf { it.weight }}",
              fontSize = 24.sp,
              modifier =
                  Modifier.align(Alignment.BottomEnd).absoluteOffset(x = spacing, y = spacing))
        }
    if (dice.diceLockState == DiceLockState.LOCKED) {
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
fun NumberCircle2(text: String, modifier: Modifier = Modifier, fontSize: TextUnit = 36.sp) {
  Box(
      modifier =
          modifier
              .fillMaxSize(0.9f)
              .padding(8.dp)
              .background(MaterialTheme.colorScheme.onTertiary, CircleShape)
              .border(BorderStroke(3.dp, MaterialTheme.colorScheme.tertiary), CircleShape)) {
        Text(
            text = text,
            fontSize = fontSize,
            modifier = Modifier.align(Alignment.Center).padding(8.dp))
      }
}

@Composable
fun FaceView(
    face: Face?,
    spacing: Dp,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    color: Color = Color.Transparent,
    showWeight: Boolean = true
) {
  val spacingMax = spacing.coerceAtMost(24.dp) // TODO refactor or not?

  Box(modifier = modifier.aspectRatio(1f).background(color, RoundedCornerShape(spacingMax))) {
    if (face?.contentDescription == IMAGE_DTO_NUMBER_CONTENT_DESCRIPTION) {
      NumberCircle2(
          face.value.toString(),
          fontSize = size.coerceAtMost(100.dp).value.sp,
          modifier = Modifier.align(Alignment.Center))
    } else {
      SizedImage(
          image = face,
          Modifier.fillMaxSize().padding(spacingMax).clip(RoundedCornerShape(spacingMax)))
      if (showWeight && face != null && face.weight > 1) {
        NumberCircle(
            face.weight.toString(),
            fontSize = spacingMax.value.sp,
            modifier = Modifier.align(Alignment.TopEnd))
      }
    }
  }
}

@Composable
fun SizedImage(image: Face?, modifier: Modifier = Modifier) {
  image?.let { imageNotNull ->
    imageNotNull.data?.let {
      ImageBitmap(image = it, contentDescription = imageNotNull.contentDescription, modifier)
    }
        ?: Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
          Text(text = imageNotNull.contentDescription)
        }
  }
      ?: ArrangedColumn(verticalArrangement = Arrangement.Center) {
        Image(
            painter = painterResource(id = R.drawable.rukaiya),
            contentDescription = "no Image",
            Modifier.size(10.dp))
        Text(text = "Roll dice", fontSize = 20.sp)
      }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {

  DynamicDicePrototypeTheme {
    FaceView(
        face = Face(contentDescription = "number", weight = 20),
        showWeight = true,
        spacing = 36.dp,
        size = 85.dp,
        color = Color.Cyan)
    // DiceView(dice = Dice(faces = getFaces(5)), size = 300.dp)
    // NumberCircle2(          "1",          fontSize = 36.sp,)
  }
}

@Composable
fun LockIcon(modifier: Modifier = Modifier) {
  Icon(imageVector = Icons.Filled.Lock, contentDescription = "LOCKED", modifier = modifier)
}
