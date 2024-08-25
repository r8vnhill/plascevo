/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.arbitrary

import property.RandomSource.nextInt
import property.arbitrary.generators.Arbitrary
import property.arbitrary.numbers.IntClassifier
import property.arbitrary.shrinkers.IntShrinker

object Ints {

    def arbInt(min: Int = Int.MinValue, max: Int = Int.MaxValue): Arbitrary[Int] = arbInt(min to max)

    /**
     * Creates an `Arbitrary[Int]` instance for generating random integers within a specified range.
     *
     * The `arbInt` function generates random integers within a given `Range`, while also providing specific edge cases,
     * a shrinker for reducing the size of generated values during testing, and a classifier for categorizing the
     * generated integers. This function is typically used in property-based testing to ensure that the generated values
     * cover both typical and boundary cases within the specified range.
     *
     * @param range The range of integers to generate. The `Range` must be non-empty, and the generated integers will be
     *              within the bounds defined by the range.
     * @return An `Arbitrary[Int]` instance that generates random integers within the specified range.
     */
    def arbInt(range: Range): Arbitrary[Int] = {
        val edgeCases = Seq(range.start, range.end, -1, 0, 1).filter(range.contains).distinct
        ArbitraryBuilder.of(_.random.nextInt(range))
            .withEdgeCases(edgeCases)
            .withShrinker(IntShrinker(range))
            .withClassifier(IntClassifier(range))
            .build()
    }
}
