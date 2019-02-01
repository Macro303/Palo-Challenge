package macro.challenge.core

import org.apache.logging.log4j.LogManager

/**
 * Created by Macro303 on 2019-Jan-30.
 */
data class Person(
	var firstName: String,
	var lastName: String,
	var zeroCount: Int
) {

	companion object {
		private val LOGGER = LogManager.getLogger(Person::class.java)
		fun calcCount(text: String): Int {
			val asciiSum = toAsciiSum(text = text)
			LOGGER.debug("ASCII Total: $asciiSum")
			val binary = toBinary(asciiSum = asciiSum)
			LOGGER.debug("Binary: $binary")
			return countZeros(input = binary)
		}

		private fun toAsciiSum(text: String): Int = text.sumBy { it.toInt() }

		private fun toBinary(asciiSum: Int): String = asciiSum.toString(radix = 2)

		private fun countZeros(input: String): Int {
			var max = 0
			var current = 0
			input.forEach {
				if (it == '0')
					current++
				else {
					if (current > max)
						max = current
					current = 0
				}
			}
			return max
		}
	}
}

