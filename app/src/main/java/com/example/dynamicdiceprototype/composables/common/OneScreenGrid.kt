import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dynamicdiceprototype.composables.screens.roll.FaceView
import com.example.dynamicdiceprototype.developer_area.getFaces
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme
import kotlin.math.floor

/** Use with modifier.weight(1f) */
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
        getMaxGridWidth(
            items.size,
            containerWidth = constraints.maxWidth,
            containerHeight = constraints.maxHeight)
    val maxSize = floor(minSize.coerceAtLeast(maxWidthPixels))
    val maxWidthDp = pxToDp(density, maxSize)
    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = maxWidthDp)) {
      items(items) { item -> onRender(item, maxWidthDp) }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun OneScreenGridPreview() {
  DynamicDicePrototypeTheme {
    Box(Modifier.height(800.dp).width(300.dp)) {
      Column {
        Box(modifier = Modifier.fillMaxWidth().height(300.dp).background(Color.Cyan))
        OneScreenGrid(items = getFaces(10), minSize = 30f, Modifier.weight(1f)) { item, maxWidth ->
          Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier.height(maxWidth).width(maxWidth).padding(4.dp)) {
                FaceView(face = item, spacing = maxWidth.div(10), color = Color.Black)
                Text(text = "$maxWidth", Modifier.background(Color.White))
              }
        }

        Box(modifier = Modifier.fillMaxWidth().height(300.dp).background(Color.Cyan))
      }
    }
  }
}
