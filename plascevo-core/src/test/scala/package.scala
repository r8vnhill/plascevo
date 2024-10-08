/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill

import cl.ravenhill.plascevo.Individual
import cl.ravenhill.plascevo.repr.{Feature, Representation}
import org.scalacheck.{Arbitrary, Gen, Shrink}

import scala.util.Random

package object plascevo:

    extension [T](iterable: Iterable[T])
        /** Applies a transformation function to consecutive pairs of elements in the iterable, returning a list of
         * results.
         *
         * The `zipWithNext` method iterates over the elements of the iterable, applying the provided `transform`
         * function to each consecutive pair of elements. The result is a list of transformed values, where each value
         * corresponds to the result of the transformation applied to a pair of elements. If the iterable contains fewer
         * than two elements, an empty list is returned.
         *
         * @param transform A function that takes two consecutive elements of type `T` and produces a result of type
         *                  `R`.
         * @tparam R The type of the result produced by the transformation function.
         * @return A list of results produced by applying the `transform` function to consecutive pairs of elements in
         *         the iterable.
         */
        def zipWithNext[R](transform: (T, T) => R): List[R] =
            val iterator = iterable.iterator
            if !iterator.hasNext then List.empty[R]
            else
                val result = List.newBuilder[R]
                var current = iterator.next()

                while iterator.hasNext do
                    val next = iterator.next()
                    result += transform(current, next)
                    current = next

                result.result()

    /** Generates a `Gen[Double]` that excludes `NaN` values.
     *
     * The `arbNonNaNDouble` method produces a generator (`Gen[Double]`) that creates `Double` values, ensuring that
     * `NaN` (Not a Number) values are excluded. This is useful in scenarios where `NaN` values would cause errors or
     * are not meaningful within the context of evolutionary algorithms.
     *
     * @param arb A generator for `Double` values, defaulting to a generator that produces `Double` values across the
     *            entire range.
     * @return A generator that produces non-NaN `Double` values.
     */
    def nonNaNDoubleGen(arb: Gen[Double] = Gen.chooseNum(Double.MinValue, Double.MaxValue)): Gen[Double] =
        arb.suchThat(!_.isNaN)

    /** Generates an instance of `Individual` for evolutionary algorithms.
     *
     * The `individualGen` method produces a generator (`Gen`) that creates instances of `Individual[T, F, R]`. The
     * generated individual consists of a `Representation` and an associated fitness value. This function is useful in
     * scenarios where you need to create test cases or simulations involving individuals in an evolutionary algorithm.
     *
     * @param representationGen A generator for the `Representation` of the individual. This is the structural aspect of
     *                          the individual.
     * @param fitnessGen        A generator for the fitness value of the individual. This represents the individual's
     *                          effectiveness or quality.
     * @tparam T The type of value held by the features in the representation.
     * @tparam F The type of the feature, which must extend `Feature[T, F]`.
     * @tparam R The type of the representation, which must extend `Representation[T, F]`.
     * @return A generator that produces instances of `Individual[T, F, R]`.
     */
    def individualGen[T, F <: Feature[T, F], R <: Representation[T, F]](
        representationGen: Gen[R],
        fitnessGen: Gen[Double] = nonNaNDoubleGen()
    ): Gen[Individual[T, F, R]] = for {
        representation <- representationGen
        fitness <- fitnessGen
    } yield Individual(representation, fitness)

    /** Generates a population of individuals for evolutionary algorithms.
     *
     * The `populationGen` method produces a generator (`Gen`) that creates a sequence of `Individual` instances. The
     * size of the generated population is randomly chosen between the specified minimum and maximum values. This is
     * useful for testing scenarios where a diverse population of individuals is required.
     *
     * @param individualGen A generator for creating particular `Individual` instances.
     * @param minSize       The minimum size of the population to generate. Defaults to `0`.
     * @param maxSize       The maximum size of the population to generate. Defaults to `100`.
     * @tparam T The type of value held by the features.
     * @tparam F The type of the feature, which must extend `Feature[T, F]`.
     * @tparam R The type of the representation, which must extend `Representation[T, F]`.
     * @return A generator that produces a sequence of `Individual[T, F, R]` instances with a size between `minSize` and
     *         `maxSize`.
     */
    def populationGen[T, F <: Feature[T, F], R <: Representation[T, F]](
        individualGen: Gen[Individual[T, F, R]],
        minSize: Int = 0,
        maxSize: Int = 100
    ): Gen[Seq[Individual[T, F, R]]] =
        Gen.chooseNum(minSize, maxSize).flatMap { size =>
            List.fill(size)(individualGen.sample.get)
        }

    /** Generates a pair of ordered values according to the specified conditions.
     *
     * The `orderedPairGen` method produces a generator (`Gen`) that creates pairs of values `(T, T)` from two given
     * generators, ensuring that the pairs are ordered according to the specified criteria. The values can be strictly
     * ordered (no equal values) and can be reversed (higher value first).
     *
     * @param a        The generator for the first value in the pair.
     * @param b        The generator for the second value in the pair.
     * @param strict   If `true`, ensures that the generated values are strictly ordered (no duplicates). Defaults to
     *                 `false`.
     * @param reversed If `true`, ensures that the generated values are in reverse order (higher value first). Defaults
     *                 to `false`.
     * @param ord      An implicit ordering for the type `T`, ensuring that values can be compared.
     * @tparam T The type of values generated and ordered.
     * @return A generator that produces pairs of values `(T, T)` ordered according to the specified conditions.
     */
    def orderedPairGen[T](
        a: Gen[T],
        b: Gen[T],
        strict: Boolean = false,
        reversed: Boolean = false
    )(using ord: Ordering[T]): Gen[(T, T)] =
        Gen.zip(a, b)
            .suchThat { (first, second) =>
                if strict then first != second else true
            }
            .suchThat { (first, second) =>
                if reversed then ord.gteq(first, second) else ord.lteq(first, second)
            }

    /** Generates a pair of ordered values from a single generator, according to the specified conditions.
     *
     * This overload of `orderedPairGen` produces a generator (`Gen`) that creates pairs of values `(T, T)` from a
     * single generator, ensuring that the pairs are ordered according to the specified criteria. The values can be
     * strictly ordered (no equal values) and can be reversed (higher value first).
     *
     * @param a        The generator for both values in the pair.
     * @param strict   If `true`, ensures that the generated values are strictly ordered (no duplicates). Defaults to
     *                 `false`.
     * @param reversed If `true`, ensures that the generated values are in reverse order (higher value first). Defaults
     *                 to `false`.
     * @param ord      An implicit ordering for the type `T`, ensuring that values can be compared.
     * @tparam T The type of values generated and ordered.
     * @return A generator that produces pairs of values `(T, T)` ordered according to the specified conditions.
     */
    def homogeneousOrderedPairGen[T](
        a: Gen[T],
        strict: Boolean = false,
        reversed: Boolean = false
    )(using ord: Ordering[T]): Gen[(T, T)] = orderedPairGen(a, a, strict, reversed)

    given noShrink[T]: Shrink[T] = Shrink.shrinkAny

    /** Generates a positive integer within a specified range.
     *
     * The `positiveIntGen` function produces a generator (`Gen`) that creates positive integers between `1` and the 
     * specified maximum value. This is useful for scenarios where a strictly positive integer is required.
     *
     * @param max The maximum value for the generated integers, inclusive. Defaults to `Int.MaxValue`.
     * @return A generator that produces positive integers within the range `[1, max]`.
     */
    def positiveIntGen(max: Int = Int.MaxValue): Gen[Int] = Gen.choose(1, max)

    /** Generates a random probability value between 0.0 and 1.0.
     *
     * The `probabilityGen` function returns a generator (`Gen[Double]`) that produces random `Double` values in the 
     * range [0.0, 1.0]. The generated values are guaranteed to be finite and non-NaN, ensuring they represent valid 
     * probabilities. This generator is particularly useful in scenarios where a random probability or chance value 
     * is needed, such as in stochastic algorithms or simulations.
     *
     * @return A generator that produces valid probability values between 0.0 and 1.0.
     */
    def probabilityGen: Gen[Double] = Gen.chooseNum(0.0, 1.0).filter(_.isFinite).filterNot(_.isNaN)

    /** Generates a random invalid probability value outside the range [0.0, 1.0].
     *
     * The `invalidProbabilityGen` function returns a generator (`Gen[Double]`) that produces random `Double` values 
     * that are considered invalid as probabilities. Specifically, the generated values are either less than 0.0 
     * or greater than 1.0, thereby falling outside the valid probability range. This generator is useful in testing 
     * scenarios where invalid probability values need to be handled, such as in input validation or error handling.
     *
     * @return A generator that produces invalid probability values, which are either less than 0.0 or greater than 1.0.
     */
    def invalidProbabilityGen: Gen[Double] = Gen.chooseNum(Double.MinValue, Double.MaxValue)
        .filter(d => d < 0.0 || d > 1.0)

    /** Generates a `Random` instance seeded with a specified or randomly generated seed.
     *
     * The `randomGen` function returns a generator (`Gen[Random]`) that produces instances of `Random`, each initialized 
     * with a specific seed value. This allows for controlled randomness in tests or simulations, where reproducibility 
     * is desired. By default, the seed is randomly generated using a `Gen[Long]` generator, which produces values across 
     * the entire range of valid `Long` values.
     *
     * @param seed A generator that produces the seed value for the `Random` instance. Defaults to a generator that 
     *             produces random `Long` values within the range of `Long.MinValue` to `Long.MaxValue`.
     * @return A generator that produces a `Random` instance seeded with the generated or provided seed value.
     */
    def randomGen(seed: Gen[Long] = Gen.chooseNum(Long.MinValue, Long.MaxValue)): Gen[Random] =
        seed.map(new Random(_))

    /**
     * Generates a pair of `Random` instances initialized with the same seed.
     *
     * The `rngPairGen` function produces a generator that creates a tuple of two `Random` instances. Both instances are
     * initialized with the same seed, ensuring that they generate identical sequences of random numbers. This is useful
     * in scenarios where deterministic behavior is needed, such as in testing or simulations.
     *
     * @param seedGen A generator for the seed value used to initialize the `Random` instances.
     *                Defaults to generating a random seed using the full range of `Long` values.
     * @return A generator that produces a tuple containing two `Random` instances initialized with the same seed.
     */
    def rngPairGen(seedGen: Gen[Long] = Gen.chooseNum(Long.MinValue, Long.MaxValue)): Gen[(Random, Random)] =
        for
            seed <- seedGen
        yield (new Random(seed), new Random(seed))

