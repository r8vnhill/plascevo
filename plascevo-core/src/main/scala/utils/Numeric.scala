package cl.ravenhill.plascevo
package utils

import scala.annotation.targetName

object Numeric {
    extension (n: Double) {

        /**
         * Checks if this `Double` is approximately equal to another `Double` within a specified threshold.
         *
         * The `=~` method compares two `Double` values and returns `true` if the absolute difference between them is
         * less than the specified `equalityThreshold`. Additionally, it handles comparisons involving
         * `Double.PositiveInfinity` and `Double.NegativeInfinity`, considering them equal only if both are the same
         * type of infinity.
         *
         * @param other             The other `Double` value to compare against.
         * @param equalityThreshold The threshold within which the two values are considered equal.
         * @return `true` if the two `Double` values are approximately equal within the threshold, `false` otherwise.
         * @example
         * {{{
         * given equalityThreshold: Double = 1e-5
         * val result = 0.123456 =~ 0.123457
         * // result: Boolean = true, because the difference is less than the threshold
         * }}}
         */
        @targetName("equalsWithThreshold")
        infix def =~(other: Double)(using equalityThreshold: Double): Boolean = {
            (n, other) match {
                case (Double.PositiveInfinity, Double.PositiveInfinity) => true
                case (Double.NegativeInfinity, Double.NegativeInfinity) => true
                case _ => Math.abs(n - other) < equalityThreshold
            }
        }

        /** Checks if this `Double` is not approximately equal to another `Double` within a specified threshold.
         *
         * The `!=~` method compares two `Double` values and returns `true` if the absolute difference between them is
         * greater than or equal to the specified `equalityThreshold`. This allows for identifying when two values
         * are considered different beyond a specified tolerance.
         *
         * @param other             The other `Double` value to compare against.
         * @param equalityThreshold The threshold beyond which the two values are considered not equal.
         * @return `true` if the two `Double` values are not approximately equal within the threshold, `false` otherwise.
         * @example
         * {{{
         * given equalityThreshold: Double = 1e-5
         * val result = 0.123456 !=~ 0.123467
         * // result: Boolean = true, because the difference is greater than the threshold
         * }}}
         */
        @targetName("notEqualsWithThreshold")
        infix def !=~(other: Double)(using equalityThreshold: Double): Boolean = !(n =~ other)
    }

    extension (n: Int) {

        /** Rounds the integer up to the nearest multiple of a specified value.
         *
         * The `roundUpToMultipleOf` method takes an integer `i` and rounds the current integer `n` up to the nearest
         * multiple of `i`. If `i` is 0, the method simply returns the original integer `n` since rounding up to a
         * multiple of zero is undefined.
         *
         * @param i The integer to which the current integer `n` should be rounded up.
         * @return The smallest integer that is a multiple of `i` and greater than or equal to `n`. If `i` is 0, returns
         *         `n`.
         * @example         
         * {{{
         * val result = 7 roundUpToMultipleOf 3
         * // result: Int = 9, because 9 is the smallest multiple of 3 that is greater than or equal to 7
         * }}}
         * @example
         * {{{
         * val result = -7 roundUpToMultipleOf 3
         * // result: Int = -6, because -6 is the smallest multiple of 3 that is greater than or equal to -7
         * }}}
         */
        infix def roundUpToMultipleOf(i: Int): Int = i match {
            case 0 => n
            case _ =>
                val remainder = n % i
                if (remainder == 0) n
                else if (n > 0) n + i - remainder
                else n - remainder
        }
    }
}
