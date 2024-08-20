package cl.ravenhill.plascevo
package ranking

import repr.{Feature, Representation}

/** A ranker that compares individuals by maximizing fitness.
 *
 * The `FitnessMaxRanker` class extends the `IndividualRanker` trait, providing a comparison mechanism that ranks
 * individuals based on their fitness values. In this ranker, individuals with higher fitness values are considered
 * better and are ranked higher. This ranker is typically used in evolutionary algorithms where the goal is to maximize
 * fitness.
 *
 * @tparam T The type of value stored by the features within the individual.
 * @tparam F The type of feature contained in the representation, which must extend [[Feature]].
 * @tparam R The type of representation used by the individual, which must extend [[Representation]].
 *
 * @example
 * {{{
 * // Example usage of FitnessMaxRanker
 * val ranker = new FitnessMaxRanker[Int, SimpleGene, SimpleRepresentation]()
 * val individual1 = Individual(SimpleRepresentation(...), fitness = 42.0)
 * val individual2 = Individual(SimpleRepresentation(...), fitness = 35.0)
 * val comparison = ranker(individual1, individual2)
 * // comparison: Int = 1 (since individual1 has a higher fitness than individual2)
 * }}}
 */
final class FitnessMaxRanker[T, F <: Feature[T, F], R <: Representation[T, F]] extends FitnessRanker[T, F, R](
    (first, second) => first.compareTo(second)
)
