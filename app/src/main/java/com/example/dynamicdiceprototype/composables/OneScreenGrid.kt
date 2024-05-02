import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dynamicdiceprototype.composables.FaceView
import com.example.dynamicdiceprototype.services.getFaces
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme

@Composable
fun <T> OneScreenGrid(
    items: List<T>,
    minSize: Float, // TODO use itemLimit
    modifier: Modifier = Modifier,
    onRender: @Composable (item: T, maxWidth: Dp) -> Unit,
) {
  BoxWithConstraints(modifier = modifier) {
    val density = LocalDensity.current
    val maxWidthPixels =
        getMaxWidth(items.size, width = constraints.maxWidth, height = constraints.maxHeight)
    val maxSize = minSize.coerceAtLeast(maxWidthPixels)
    val maxWidthDp = pxToDp(density, maxSize) // this is 80.dp

    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = maxWidthDp)) {
      items(items) { item -> onRender(item, maxWidthDp) }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun OneScreenGridPreview() {
  DynamicDicePrototypeTheme {
    Box(Modifier.height(800.dp).width(210.dp)) {
      OneScreenGrid(items = getFaces(20), minSize = 110f) { item, maxWidth -> // maxWidth is 80.dp
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier.height(maxWidth)
                    .width(maxWidth)
                    .padding(4.dp)) { // Why is this not filling 80.dp, but just the Text height?
              FaceView(face = item, spacing = maxWidth.div(10), color = Color.Black)
            }
      }
    }
  }
}
