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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dynamicdiceprototype.Dice
import com.example.dynamicdiceprototype.DiceState
import com.example.dynamicdiceprototype.DiceViewModel

@Composable
fun DiceView(dice: Dice, size: Dp, modifier: Modifier = Modifier) {
  val viewModel: DiceViewModel = viewModel<DiceViewModel>()

  Log.i("MyApp", "Recompose DiceView dices $dice")
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
          Image(
              painter =
                  painterResource(
                      id = dice.current?.imageId ?: 0), // TODO make current not null or something
              contentDescription = dice.current?.data ?: "No Dice",
              modifier = Modifier.fillMaxSize().padding(16.dp))
        }
    if (dice.state == DiceState.LOCKED) {
      LockIcon(modifier = Modifier.align(Alignment.TopEnd).size(36.dp))
    }
  }
}

@Composable
fun LockIcon(modifier: Modifier = Modifier) {
  Icon(imageVector = Icons.Filled.Lock, contentDescription = "LOCKED", modifier = modifier)
}
