package cl.ravenhill.plascevo

import org.scalacheck.Gen

package object utils {
    /**
     * Generates a `Gen[Double]` that filters out `NaN` values from the provided generator.
     *
     * This method ensures that the generated doubles do not include `NaN` (Not a Number) values, which can be
     * undesirable in certain computations or data processing scenarios.
     *
     * @param gen The original `Gen[Double]` from which values are generated.
     * @return A `Gen[Double]` that produces only non-NaN values.
     */
    def nonNanDouble(gen: Gen[Double]): Gen[Double] = gen.filterNot(_.isNaN)

    /**
     * Generates a `Gen[(Double, Double)]` that filters out `NaN` values from both elements of the provided pair
     * generator.
     *
     * This method takes a generator that produces `Double` values and returns a new generator where both elements in
     * the pair are guaranteed to be non-NaN values. This is useful for scenarios where `NaN` values are not desired in
     * pairs of double values, such as in mathematical computations or data processing.
     *
     * @param gen The original `Gen[Double]` from which pairs of doubles are generated.
     * @return A `Gen[(Double, Double)]` that produces pairs of non-NaN values.
     */
    def nonNanDoublePairGen(gen: Gen[Double]): Gen[(Double, Double)] = Gen.zip(nonNanDouble(gen), nonNanDouble(gen))
}
