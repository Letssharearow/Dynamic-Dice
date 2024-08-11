package com.example.dynamicdiceprototype.composables

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dynamicdiceprototype.R
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

@Composable
fun DiceButtonM3(onRollClicked: () -> Unit, modifier: Modifier = Modifier) {
  var spinCount by remember { mutableStateOf(0) }
  val rotation by
      animateFloatAsState(
          targetValue = 900f * spinCount,
          animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing))

  // Use Material 3 Button styles
  FilledTonalButton(
      onClick = {
        spinCount++
        onRollClicked()
      },
      shape = RoundedCornerShape(12.dp),
      // Customize colors for your button here
      colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
      modifier = modifier) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(50.dp)
                .background(MaterialTheme.colorScheme.primary)) {
              Image(
                  painter = painterResource(id = R.drawable.rolling_dices_transparent),
                  contentDescription = "rolling dices button",
                  modifier = Modifier
                      .size(100.dp)
                      .graphicsLayer { rotationZ = rotation })
            }
      }
}

@Preview
@Composable
private fun Preview() {
    DynamicDicePrototypeTheme {
        DiceButtonM3(onRollClicked = {})
    }
}