import android.content.res.Resources
import android.util.TypedValue
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

fun getMaxGridWidth(count: Int, containerWidth: Int, containerHeight: Int): Float {
  var currentMaxWidth = 0f
  var currentMaxHeight = 0f
  for (i in 1..count) { // i = 2 means how much space for 2 columns
    currentMaxWidth =
        max(
            currentMaxWidth,
            min(
                containerHeight.toFloat() / ceil(count / i.toFloat()), // "max height for i columns
                containerWidth.toFloat() / i // max width for i columns
                ))
  }

  for (i in 1..count) { // i = 2 means how much space for 2 rows
    currentMaxHeight =
        max(
            currentMaxHeight,
            min(
                containerWidth.toFloat() / ceil(count / i.toFloat()), // "max height for i rows
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

fun <T> List<T>.selectNext(currentStateIndex: Int?): Int? {
  return ((currentStateIndex ?: -1) + 1).takeIf { it >= 0 && it < this.size }
}

fun weightedRandom(weights: List<Double>): Int {
  val totalWeight = weights.sum()
  val randomValue = Random.nextDouble(totalWeight)
  var accumulatedWeight = 0.0

  for (i in weights.indices) {
    accumulatedWeight += weights[i]
    if (randomValue < accumulatedWeight) {
      return i
    }
  }
  return weights.lastIndex // Should not happen, but just in case
}

/**
 * @param start inclusive
 * @param base the middle of the curve
 * @param end inclusive
 * @param curve the curve of the distribution
 * @param addLeadingZeros if true, the list will start with 0.0 for 0
 * @example getWeightsInRange(1, 2, 4, 0.5, true) will return [0.0, 0.5, 1.0, 0.75, 0.5]
 */
fun getWeightsInRange(
    start: Int,
    base: Int,
    end: Int,
    curve: Double = 0.5,
    addLeadingZeros: Boolean = true
): List<Double> {
  if (start >= end || start > base || end < base) {
    return listOf()
  }
  val list = mutableListOf<Double>()
  if (addLeadingZeros) {
    for (i in 0..end) {
      if (i < start) {
        list.add(0.0)
        continue
      }
      list.add(curve.pow(abs(base - i)))
    }
  } else {
    for (i in start..end) {
      list.add(curve.pow(abs(base - i)))
    }
  }
  return list
}

fun main() {
  println(getMaxGridWidth(1, 411, 814))
  println(getMaxGridWidth(2, 411, 814))
  println(getMaxGridWidth(4, 411, 814))
  println(getMaxGridWidth(9, 411, 814))
  println(getMaxGridWidth(16, 411, 814))
}
