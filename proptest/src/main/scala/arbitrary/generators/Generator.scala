/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.checkall
package arbitrary.generators

import arbitrary.{Classifier, EdgeConfiguration}
import arbitrary.generators.Sample.asSample

sealed trait Generator[+T] {
    def classifier: Option[Classifier[? <: T]] = None

    def generate(
        rs: RandomSource,
        edgeConfig: EdgeConfiguration = EdgeConfiguration.default
    ): LazyList[Sample[T]] = this match {
        case arb: Arbitrary[T] =>
            val samples = arb.samples(rs).iterator

            LazyList.continually {
                val isEdgeCase = rs.random.between(0.0, 1.0) < edgeConfig.edgeCasesGenerationProbability
                if (isEdgeCase) {
                    arb.edgeCases(rs).map(asSample).getOrElse(samples.next())
                } else {
                    samples.next()
                }
            }

        case _ => ???
    }
}

/**
 * Trait `Arbitrary` represents a generator for values of type `T` with support for edge cases.
 *
 * The `Arbitrary` trait extends the `Generator` trait and provides additional functionality for generating values of
 * type `T`, including the ability to produce edge cases and to continuously generate samples.
 *
 * @tparam T The type of values generated by this `Arbitrary` instance.
 */
trait Arbitrary[+T] extends Generator[T] {

    /**
     * Generates an edge case value if one exists.
     *
     * The `edgeCases` method attempts to produce a value of type `T` that is considered an edge case. Edge cases are
     * special values that are likely to trigger boundary conditions or corner cases in the system being tested. If
     * no specific edge cases are available, this method may return `None`.
     *
     * @param rs The `RandomSource` used to influence the generation of the edge case value.
     * @return An `Option` containing an edge case value if one exists, or `None` if no edge cases are defined.
     */
    def edgeCases(rs: RandomSource): Option[T]

    /**
     * Generates a sample value of type `T`.
     *
     * The `sample` method produces a single `Sample[T]`, which represents a generated value of type `T`. This method
     * is used to generate the main values during property-based testing, utilizing the provided `RandomSource` to
     * ensure variability in the generated values.
     *
     * @param rs The `RandomSource` used to generate the sample value.
     * @return A `Sample[T]` representing the generated value.
     */
    def sample(rs: RandomSource): Sample[T]

    /**
     * Generates an infinite stream of sample values.
     *
     * The `samples` method returns an `Iterator` that continuously produces sample values of type `T`. This method is
     * useful when you need to generate a large number of test cases or when you want to explore the space of possible
     * values by iterating over them indefinitely. The values are generated using the provided `RandomSource`.
     *
     * @param rs The `RandomSource` used to generate the sample values. Defaults to a `RandomSource` created with the
     *           `default` method.
     * @return An `Iterator` that produces an infinite stream of sample values.
     * @example
     * {{{
     *   val arbitraryInt: Arbitrary[Int] = ...
     *   val randomSource = RandomSource.default
     *   val samples = arbitraryInt.samples(randomSource)
     *   samples.take(10).foreach(println) // Prints 10 random integers
     * }}}
     */
    def samples(rs: RandomSource = RandomSource.default): Iterator[Sample[T]] = Iterator.continually(sample(rs))
}
