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

fun List<Double>.binarySearchCeiling(target: Double): Int {
  var left = 0
  var right = this.size - 1

  while (left <= right) {
    val mid = (left + right) / 2
    when {
      this[mid] == target -> return mid
      this[mid] < target -> left = mid + 1
      else -> {
        right = mid - 1
        if (right < 0 || this[right] <= target) {
          return mid
        }
      }
    }
  }

  return -1 // Element not found
}

fun weightedRandom(weights: List<Double>): Int {
  val cumulativeWeights = weights.runningFold(0.0) { acc, weight -> acc + weight }.drop(1)
  val randomValue = Random.nextDouble(cumulativeWeights.last())
  return cumulativeWeights.binarySearchCeiling(randomValue)
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
  if (start > base) {
    return emptyList()
  }
  val weights = (start..end).map { curve.pow(abs(base - it)) }
  return if (addLeadingZeros && start > 0) {
    List(start) { 0.0 } + weights
  } else {
    weights
  }
}

fun main() {
  println(getMaxGridWidth(1, 411, 814))
  println(getMaxGridWidth(2, 411, 814))
  println(getMaxGridWidth(4, 411, 814))
  println(getMaxGridWidth(9, 411, 814))
  println(getMaxGridWidth(16, 411, 814))
}
