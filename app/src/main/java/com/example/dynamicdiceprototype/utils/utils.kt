import kotlin.math.max
import kotlin.math.min

fun getMaxWidth(count: Int, width: Int, height: Int): Float {
  var currentMaxWidth = 0F
  for (i in 1..count) {
    currentMaxWidth = max(currentMaxWidth, min((height.toFloat() * i) / count, width.toFloat() / i))
  }
  return currentMaxWidth
}

// TODO Add tests

fun main() {
  println(getMaxWidth(1, 411, 814))
  println(getMaxWidth(2, 411, 814))
  println(getMaxWidth(4, 411, 814))
  println(getMaxWidth(9, 411, 814))
  println(getMaxWidth(16, 411, 814))
}
