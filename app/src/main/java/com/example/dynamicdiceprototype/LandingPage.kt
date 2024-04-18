import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dynamicdiceprototype.Dice
import com.example.dynamicdiceprototype.DiceViewModel

@Composable
fun LandingPage(dices: List<Dice>, name: String, modifier: Modifier = Modifier) {
  val viewModel: DiceViewModel = viewModel<DiceViewModel>()

  Log.i("MyApp", "Recompose Landing Page dices $dices")
  Column() {
    DiceBundle(dices, name, modifier = Modifier.weight(1f))
    DiceButtonM3(
        onRollClicked = { viewModel.rollDices() },
        modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 16.dp))
  }
}
