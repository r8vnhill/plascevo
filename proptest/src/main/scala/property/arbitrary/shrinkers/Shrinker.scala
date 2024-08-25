/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.arbitrary.shrinkers

/**
 * A trait for defining shrinking behavior in property-based testing.
 *
 * The `Shrinker` trait is used to define a strategy for "shrinking" values in property-based testing. 
 * Shrinking is the process of reducing a complex or large failing test case to a simpler or smaller one that still
 * fails. This helps in isolating the minimal failing case, making it easier to understand and debug the problem.
 *
 * @tparam A The type of value that can be shrunk by this shrinker.
 */
trait Shrinker[A] {

    /**
     * Shrinks a given value to a sequence of smaller or simpler values.
     *
     * The `shrink` method is responsible for generating a sequence of candidate values that are simpler or smaller
     * than the original value, yet still relevant for testing. These values are used to find the minimal failing case
     * in property-based testing.
     *
     * @param value The value to be shrunk.
     * @return A sequence of shrunk values.
     */
    def shrink(value: A): Seq[A]
}
