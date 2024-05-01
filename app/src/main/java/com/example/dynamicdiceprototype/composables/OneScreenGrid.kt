import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

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
    val maxWidthDp = pxToDp(density, maxSize)

    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = maxWidthDp)) {
      items(items) { item -> onRender(item, maxWidthDp) }
    }
  }
}