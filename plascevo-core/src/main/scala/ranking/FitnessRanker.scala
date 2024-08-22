package cl.ravenhill.plascevo
package ranking

import repr.{Feature, Representation}

/** A generic ranker for individuals based on a custom fitness comparison function.
 *
 * The `FitnessRanker` trait extends the `IndividualRanker` trait, allowing the ranking of individuals in an
 * evolutionary algorithm based on a custom comparison function. This trait provides flexibility in how individuals
 * are compared by allowing the user to define the comparison logic through the `compareFunction` parameter.
 *
 * @tparam T The type of value stored by the features within the individual.
 * @tparam F The type of feature contained in the representation, which must extend [[Feature]].
 * @tparam R The type of representation used by the individual, which must extend [[Representation]].
 * @param compareFunction A function that compares two fitness values. The function should return:
 *                        - A negative integer if the first fitness is considered less than the second.
 *                        - Zero if both fitness values are considered equal.
 *                        - A positive integer if the first fitness is considered greater than the second.
 *
 * @example
 * {{{
 * // Example usage of FitnessRanker for maximizing fitness
 * val maxFitnessRanker = new FitnessRanker[Int, SimpleGene, SimpleRepresentation](_ compareTo _)
 *
 * // Example usage of FitnessRanker for minimizing fitness
 * val minFitnessRanker = new FitnessRanker[Int, SimpleGene, SimpleRepresentation]((x, y) => y compareTo x)
 *
 * val individual1 = Individual(SimpleRepresentation(...), fitness = 42.0)
 * val individual2 = Individual(SimpleRepresentation(...), fitness = 35.0)
 * val comparison = maxFitnessRanker.compare(individual1, individual2)
 * // comparison: Int = 1 (since individual1 has a higher fitness than individual2 when maximizing)
 * }}}
 */
trait FitnessRanker[T, F <: Feature[T, F], R <: Representation[T, F]](
    compareFunction: (Double, Double) => Int
) extends IndividualRanker[T, F, R] {

    /** Compares two individuals based on their fitness using a custom comparison function.
     *
     * The `compare` method uses the provided `compareFunction` to compare the fitness values of two individuals.
     * This allows for customized ranking logic, such as maximizing or minimizing fitness, or other complex
     * comparisons based on specific criteria.
     *
     * @param first  The first individual to compare.
     * @param second The second individual to compare.
     * @return An integer indicating the relative fitness of the two individuals, as determined by the
     *         `compareFunction`.
     */
    override def compare(
        first: Individual[T, F, R],
        second: Individual[T, F, R]
    ): Int = compareFunction(first.fitness, second.fitness)
}
