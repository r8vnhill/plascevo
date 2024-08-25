/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.arbitrary.shrinkers

/**
 * A `Shrinker` implementation for `Int` values within a specified range.
 *
 * The `IntShrinker` class provides a strategy for shrinking integer values during property-based testing. 
 * Shrinking is the process of reducing a failing test case to a simpler or smaller value that still fails, 
 * helping to identify the minimal failing case. This implementation shrinks integers within a specified `Range`, 
 * attempting to generate candidate values that are closer to zero or other simple values.
 *
 * @param range The range of integers that the shrinker operates within. Only values within this range will be generated
 *              as shrunk values.
 */
final class IntShrinker(val range: Range) extends Shrinker[Int] {

    /**
     * Shrinks an integer value to a sequence of smaller or simpler values within the specified range.
     *
     * The `shrink` method attempts to generate a sequence of candidate integers that are simpler or smaller than the 
     * original value. These candidates include values like 0, 1, -1, and fractions of the original value. The method 
     * also generates a series of values close to the original value by decrementing it. All shrunk values are filtered 
     * to ensure they fall within the specified `range`.
     *
     * @param value The integer value to be shrunk.
     * @return A sequence of shrunk integer values within the specified range.
     */
    override def shrink(value: Int): Seq[Int] = value match
        case 0 => Nil
        case 1 | -1 => Seq(0).filter(range.contains)
        case _ =>
            val a = Array(0, 1, -1, math.abs(value), value / 3, value / 2, value * 2 / 3)
            val b = (1 to 5).map(value - _).reverse.filter(_ > 0)
            (a ++ b).distinct.filterNot(_ == value).filter(range.contains)
}
