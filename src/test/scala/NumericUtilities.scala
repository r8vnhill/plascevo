package cl.ravenhill.plascevo

import org.scalacheck.Gen

import java.nio.charset.MalformedInputException

/** A utility object that provides generators for numeric values, designed for property-based testing.
 *
 * The `NumericUtilities` object contains a collection of methods for generating integer values under specific
 * constraints. These utilities are particularly useful for creating test data that needs to adhere to certain numeric
 * conditions, such as being non-negative or negative.
 */
object NumericUtilities {

    /** Generates a non-negative integer within the specified range.
     *
     * The `nonNegativeIntGen` method returns a generator that produces non-negative integers ranging from `0` up to and
     * including the specified maximum value. This generator is useful in scenarios where tests require values that are
     * zero or positive, such as counts, sizes, or other quantities that cannot be negative.
     *
     * @param max The maximum value for the generated integers, inclusive. This must be a non-negative value and
     *            defaults to `Int.MaxValue`.
     * @return A generator that produces non-negative integers within the specified range.
     * @throws IllegalArgumentException if `max` is negative.
     */
    def nonNegativeIntGen(max: Int = Int.MaxValue): Gen[Int] = {
        require(max >= 0, "The maximum value must be non-negative.")
        Gen.choose(0, max)
    }

    /** Generates a negative integer within the specified range.
     *
     * The `negativeIntGen` method returns a generator that produces negative integers ranging from the specified
     * minimum value up to and including `0`. This generator is useful when testing scenarios that require negative
     * values, such as offsets, debts, or other quantities that cannot be positive.
     *
     * @param min The minimum value for the generated integers, inclusive. This must be a non-positive value and defaults
     *            to `Int.MinValue`.
     * @return A generator that produces negative integers within the specified range.
     * @throws IllegalArgumentException if `min` is positive.
     */
    def negativeIntGen(min: Int = Int.MinValue): Gen[Int] = {
        require(min <= 0, "The minimum value must be non-positive.")
        Gen.choose(min, 0)
    }
}
