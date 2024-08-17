package cl.ravenhill

import cl.ravenhill.plascevo.Individual
import cl.ravenhill.plascevo.repr.{Feature, Representation}
import org.scalacheck.{Arbitrary, Gen}

/** A package object for the `plascevo` package, containing utility functions for evolutionary algorithm testing.
 *
 * This package includes functions for generating specific types of data used in property-based testing, such as
 * non-NaN `Double` values.
 */
package object plascevo:

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
    def arbNonNaNDouble(arb: Gen[Double] = Gen.chooseNum(Double.MinValue, Double.MaxValue)): Gen[Double] =
        arb.suchThat(!_.isNaN)

/** Generates an instance of `Individual` for evolutionary algorithms.
 *
 * The `individualGen` method produces a generator (`Gen`) that creates instances of `Individual[T, F, R]`. The
 * generated individual consists of a `Representation` and an associated fitness value. This function is useful in
 * scenarios where you need to create test cases or simulations involving individuals in an evolutionary algorithm.
 *
 * @param representationGen A generator for the `Representation` of the individual. This is the structural aspect of the
 *                          individual.
 * @param fitnessGen        A generator for the fitness value of the individual. This represents the individual's
 *                          effectiveness or quality.
 * @tparam T The type of value held by the features in the representation.
 * @tparam F The type of the feature, which must extend `Feature[T, F]`.
 * @tparam R The type of the representation, which must extend `Representation[T, F]`.
 * @return A generator that produces instances of `Individual[T, F, R]`.
 */
def individualGen[T, F <: Feature[T, F], R <: Representation[T, F]](
    representationGen: Gen[R],
    fitnessGen: Gen[Double]
): Gen[Individual[T, F, R]] = for {
    representation <- representationGen
    fitness <- fitnessGen
} yield Individual(representation, fitness)
