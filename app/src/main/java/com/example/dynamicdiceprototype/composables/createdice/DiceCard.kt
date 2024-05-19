package com.example.dynamicdiceprototype.composables.createdice

import OneScreenGrid
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.example.dynamicdiceprototype.composables.FaceView
import com.example.dynamicdiceprototype.composables.NumberCircle
import com.example.dynamicdiceprototype.data.Dice
import com.example.dynamicdiceprototype.services.getDices
import com.example.dynamicdiceprototype.services.getFaces
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

@Composable
fun DiceCard(dice: Dice, isCompact: Boolean, modifier: Modifier = Modifier) {
  val facesSum = dice.faces.sumOf { it.weight }

  Surface(
      shadowElevation = 8.dp,
      color = dice.backgroundColor,
      modifier =
          modifier
              .border(
                  BorderStroke(2.dp, MaterialTheme.colorScheme.secondary),
                  RoundedCornerShape(16.dp))
              .clip(RoundedCornerShape(16.dp))) {
        if (isCompact) {
          CompactDiceCard(dice.name, facesSum)
        } else {
          DetailedDiceCard(dice, facesSum)
        }
      }
}

@Composable
private fun CompactDiceCard(name: String, facesSum: Int) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceAround,
      modifier = Modifier.fillMaxWidth()) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleSmall,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(0.66F)
   )

        NumberCircle(facesSum.toString())
      }
}

@Composable
private fun DetailedDiceCard(dice: Dice, facesSum: Int) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(8.dp).background(dice.backgroundColor)) {
        Text(
            text = dice.name,
            style = MaterialTheme.typography.displayMedium,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(0.66F))
        Spacer(modifier = Modifier.width(8.dp))
        DicePreview(dice, facesSum, Modifier.fillMaxWidth().aspectRatio(1f))
      }
}

@Composable
fun DicePreview(dice: Dice, facesSum: Int, modifier: Modifier = Modifier) {
  Box(
      modifier
          .fillMaxWidth()
          .aspectRatio(1f)
          .border(BorderStroke(2.dp, Color.Gray), RoundedCornerShape(16.dp))
          .clip(RoundedCornerShape(16.dp))) {
        OneScreenGrid(
            items = dice.faces.let { if (it.size >= 10) it.subList(0, 10) else it },
            minSize = 10f,
        ) { face, maxWidthDp ->
          Box(
              contentAlignment = Alignment.Center,
              modifier = androidx.compose.ui.Modifier.size(maxWidthDp)) {
                FaceView(
                    face,
                    showWeight = false,
                    spacing = maxWidthDp.div(10),
                    color = dice.backgroundColor,
                    modifier = Modifier.padding(maxWidthDp.div(20)))
              }
        }
        if (facesSum > 2) {
          CircleOverlay("$facesSum")
        }
      }
}

@Composable
private fun CircleOverlay(text: String) {
  Box(
      modifier =
          Modifier.wrapContentSize()
              .aspectRatio(1f)
              .padding(20.dp) // TODO better size adjustment, wrapContentSize
              // didnt work
              .background(Color(0x80000000), CircleShape)
              .border(BorderStroke(2.dp, Color.White), CircleShape)) {
        Text(
            text = text,
            style = MaterialTheme.typography.displayMedium,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center).wrapContentSize())
      }
}

class previewProvider : PreviewParameterProvider<Int> {
  override val values: Sequence<Int>
    get() = sequenceOf(1, 2, 3, 4, 5, 6, 10, 40, 45)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DiceCardPeview(@PreviewParameter(previewProvider::class) facesCount: Int) {
  DynamicDicePrototypeTheme {
    DiceCard(getDices(1).first().copy(faces = getFaces(facesCount)), false)
  }
}

@Preview(showBackground = true, backgroundColor = 0, widthDp = 120, heightDp = 120)
@Composable
fun CircleOverlayPreview() {
  DynamicDicePrototypeTheme { CircleOverlay(text = "12") }
}
