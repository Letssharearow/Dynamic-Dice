import android.content.res.Resources
import android.util.TypedValue
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import kotlin.math.max
import kotlin.math.min

fun getMaxGridWidth(count: Int, containerWidth: Int, containerHeight: Int): Float {
  var currentMaxWidth = 0f
  var currentMaxHeight = 0f
  for (i in 1..count) { // i = 2 means how much space for 2 columns
    currentMaxWidth =
        max(
            currentMaxWidth,
            min(
                containerHeight.toFloat() / (count / i), // "max height for i columns
                containerWidth.toFloat() / i // max width for i columns
                ))
  }

  for (i in 1..count) { // i = 2 means how much space for 2 rows
    currentMaxHeight =
        max(
            currentMaxHeight,
            min(
                containerWidth.toFloat() / (count / i), // "max height for i rows
                containerHeight.toFloat() / i // max width for i rows
                ))
  }
  return min(currentMaxWidth, currentMaxHeight)
}

fun pxToDp(density: Density, MaxSize: Float): Dp {
  with(density) {
    return MaxSize.toDp()
  }
}

fun pxToDp2(resources: Resources, pixels: Float) {
  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixels, resources.displayMetrics)
}

fun toPx(dp: Dp) {
  dp.value
}

fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

// TODO Add tests

fun main() {
  println(getMaxGridWidth(1, 411, 814))
  println(getMaxGridWidth(2, 411, 814))
  println(getMaxGridWidth(4, 411, 814))
  println(getMaxGridWidth(9, 411, 814))
  println(getMaxGridWidth(16, 411, 814))
}
