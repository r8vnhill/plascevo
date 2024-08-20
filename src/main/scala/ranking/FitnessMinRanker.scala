package cl.ravenhill.plascevo
package ranking

import repr.{Feature, Representation}

/** A ranker that compares individuals by minimizing fitness.
 *
 * The `FitnessMinRanker` class extends the `IndividualRanker` trait, providing a comparison mechanism that ranks
 * individuals based on their fitness values. In this ranker, individuals with lower fitness values are considered
 * better and are ranked higher. This ranker is typically used in evolutionary algorithms where the goal is to
 * minimize fitness, such as in optimization problems where lower values are preferable.
 *
 * @tparam T The type of value stored by the features within the individual.
 * @tparam F The type of feature contained in the representation, which must extend [[Feature]].
 * @tparam R The type of representation used by the individual, which must extend [[Representation]].
 *
 * @example
 * {{{
 * // Example usage of FitnessMinRanker
 * val ranker = new FitnessMinRanker[Int, SimpleGene, SimpleRepresentation]()
 * val individual1 = Individual(SimpleRepresentation(...), fitness = 42.0)
 * val individual2 = Individual(SimpleRepresentation(...), fitness = 35.0)
 * val comparison = ranker(individual1, individual2)
 * // comparison: Int = -1 (since individual2 has a lower fitness than individual1)
 * }}}
 */
final class FitnessMinRanker[T, F <: Feature[T, F], R <: Representation[T, F]] extends FitnessRanker[T, F, R](
    (first, second) => second.compareTo(first)
)
