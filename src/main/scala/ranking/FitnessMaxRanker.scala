package cl.ravenhill.plascevo
package ranking

import repr.{Feature, Representation}

/** A final class that ranks individuals by maximizing fitness.
 *
 * The `FitnessMaxRanker` class extends the `IndividualRanker` trait, providing a comparison mechanism that ranks
 * individuals based on their fitness values, with higher fitness being better. It compares two individuals and
 * returns an integer indicating their relative fitness.
 *
 * @tparam T The type of value stored by the feature.
 * @tparam F The kind of feature stored in the representation, which must implement [[Feature]].
 * @tparam R The type of representation used by the individual, which must implement [[Representation]].
 */
final class FitnessMaxRanker[T, F <: Feature[T, F], R <: Representation[T, F]] extends IndividualRanker[T, F, R]:

    /** Compares two individuals based on their fitness.
     *
     * This method compares the fitness values of two individuals, returning a negative integer if the first
     * individual is less fit, zero if they are equally fit, or a positive integer if the first individual is more fit.
     * The comparison is such that individuals with higher fitness are ranked higher.
     *
     * @param first  The first individual to compare.
     * @param second The second individual to compare.
     * @return A negative integer, zero, or a positive integer if the first individual's fitness is less than, equal to,
     *         or greater than the second individual's fitness, respectively.
     */
    override def compare(
        first: Individual[T, F, R],
        second: Individual[T, F, R]
    ): Int = first.fitness.compareTo(second.fitness)
