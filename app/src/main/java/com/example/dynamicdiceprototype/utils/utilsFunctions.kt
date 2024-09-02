import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.random.Random

/**
 * Calculates the maximum grid width that can fit within a given container while maintaining a
 * rectangular grid shape.
 *
 * @param count The total number of items to be arranged in the grid.
 * @param containerWidth The width of the container in pixels.
 * @param containerHeight The height of the container in pixels.
 * @return The maximum grid width in pixels.
 *
 * **Example:**
 *
 * ```kotlin
 * val maxWidth = getMaxGridWidth(count = 8, containerHeight = 80, containerWidth = 20)
 * // maxWidth will be 10f, indicating a 2x4 grid
 * ```
 */
fun getMaxGridWidth(count: Int, containerWidth: Int, containerHeight: Int): Float {
  val containerWidthFloat = containerWidth.toFloat()
  val containerHeightFloat = containerHeight.toFloat()

  fun calculateMaxWidth(width: Float, height: Float): Float {
    var maxWidth = 0f
    for (i in 1..count) {
      maxWidth = maxOf(maxWidth, minOf(height / ceil(count / i.toFloat()), width / i))
    }
    return maxWidth
  }

  val maxWidth = calculateMaxWidth(containerWidthFloat, containerHeightFloat)
  val maxHeight = calculateMaxWidth(containerHeightFloat, containerWidthFloat)

  return minOf(maxWidth, maxHeight)
}

fun pxToDp(density: Density, pixel: Float): Dp {
  with(density) {
    return pixel.toDp()
  }
}

fun <T> List<T>.selectNext(currentStateIndex: Int?): Int? {
  return ((currentStateIndex ?: -1) + 1).takeIf { it >= 0 && it < this.size }
}

/**
 * Performs a binary search on a sorted list of doubles to find the index of the smallest element
 * that is greater than or equal to the target value.
 *
 * @param target The target value to search for.
 * @return The index of the smallest element that is greater than or equal to the target, or -1 if
 *   the target is not found.
 *
 * **Example:**
 *
 * ```kotlin
 * val list = listOf(1.0, 2.5, 3.7, 5.1)
 * val index = list.binarySearchCeiling(3.0)
 * // index will be 2 (the index of 3.7)
 * ```
 */
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

/**
 * Selects a random index from a list based on the given weights.
 *
 * @param weights A list of weights representing the relative probabilities of each index.
 * @return The randomly selected index.
 *
 * **Example:**
 *
 * ```kotlin
 * val weights = listOf(1, 3, 1)
 * val randomIndex = weightedRandom(weights)
 * // randomIndex will be 0, 1, or 2 with probabilities 0.2, 0.6, and 0.2, respectively.
 * ```
 */
fun weightedRandom(weights: List<Double>): Int {
  val cumulativeWeights = weights.runningFold(0.0) { acc, weight -> acc + weight }.drop(1)
  val randomValue = Random.nextDouble(cumulativeWeights.last())
  return cumulativeWeights.binarySearchCeiling(randomValue)
}

/**
 * Generates a list of weights based on a specified range and curve.
 *
 * @param start inclusive start of List
 * @param base the middle of the curve
 * @param end inclusive end of List
 * @param curve A value between 0 and 1 that determines the shape of the weight curve. A higher
 *   value results in a flatter curve, while a lower value results in a sharper curve.
 * @param addLeadingZeros Whether to add leading zeros to the list if `start` is greater than 0.
 * @return A list of weights corresponding to the indices in the specified range.
 *
 * **Example:**
 *
 * ```kotlin
 * val weights = getWeightsInRange(1, 2, 4, 0.5, true)
 * // weights will be [0.0, 0.5, 1.0, 0.75, 0.5].
 * ```
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
