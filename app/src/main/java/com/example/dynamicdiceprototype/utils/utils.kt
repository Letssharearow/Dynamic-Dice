import android.content.res.Resources
import android.util.TypedValue
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import kotlin.math.max
import kotlin.math.min

fun getMaxWidth(count: Int, width: Int, height: Int): Float {
  var currentMaxWidth = 0F
  for (i in 1..count) {
    currentMaxWidth = max(currentMaxWidth, min((height.toFloat() * i) / count, width.toFloat() / i))
  }
  return currentMaxWidth
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
  println(getMaxWidth(1, 411, 814))
  println(getMaxWidth(2, 411, 814))
  println(getMaxWidth(4, 411, 814))
  println(getMaxWidth(9, 411, 814))
  println(getMaxWidth(16, 411, 814))
}
