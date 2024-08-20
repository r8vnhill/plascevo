package cl.ravenhill.plascevo
package ranking

import repr.{Feature, Representation}

/** Trait for ranking individuals in an evolutionary algorithm based on their fitness.
 *
 * The `IndividualRanker` trait provides the interface for comparing and sorting individuals in an evolutionary
 * algorithm. This ranking is crucial for selection processes, where individuals are chosen based on their fitness to
 * form the next generation.
 *
 * Implementing classes must define the `compare` method, which compares two individuals based on their fitness values.
 * The trait also provides a built-in `Ordering` instance that is used to sort populations in descending order of
 * fitness.
 *
 * @tparam T The type of value stored by the features within the individual.
 * @tparam F The type of feature contained within the representation, which must extend [[Feature]].
 * @tparam R The type of representation used by the individual, which must extend [[Representation]].
 *
 * @example
 * {{{
 * // Example implementation of a simple ranker based on fitness
 * class SimpleRanker extends IndividualRanker[Int, SimpleGene, SimpleRepresentation] {
 *   override def compare(first: Individual[Int, SimpleGene, SimpleRepresentation],
 *                        second: Individual[Int, SimpleGene, SimpleRepresentation]): Int = {
 *     first.fitness.compareTo(second.fitness)
 *   }
 * }
 *
 * val ranker = new SimpleRanker()
 * val population: Seq[Individual[Int, SimpleGene, SimpleRepresentation]] = ...
 * val sortedPopulation = ranker.sort(population)
 * // sortedPopulation: Seq[Individual[Int, SimpleGene, SimpleRepresentation]] = ...
 * }}}
 */
trait IndividualRanker[T, F <: Feature[T, F], R <: Representation[T, F]] {

    /** Given instance of Ordering for comparing individuals by their fitness.
     *
     * This `Ordering` instance is automatically derived from the `compare` method and is used to sort populations of
     * individuals. It sorts individuals in descending order of fitness, meaning that the fittest individuals are
     * placed first.
     */
    given ordering: Ordering[Individual[T, F, R]] with
        def compare(first: Individual[T, F, R], second: Individual[T, F, R]): Int =
            IndividualRanker.this.compare(first, second)

    /** Compares two individuals based on their fitness.
     *
     * Implementing classes must define this method to compare the fitness of two individuals. The method should return:
     * - A negative integer if `first` is less fit than `second`.
     * - Zero if `first` and `second` have equal fitness.
     * - A positive integer if `first` is more fit than `second`.
     *
     * @param first  The first individual to compare.
     * @param second The second individual to compare.
     * @return An integer indicating the relative fitness of the two individuals.
     */
    def compare(first: Individual[T, F, R], second: Individual[T, F, R]): Int

    /** Applies the comparison of two individuals.
     *
     * This method provides an alias for the `compare` method, allowing the `IndividualRanker` to be used as a function.
     *
     * @param first  The first individual to compare.
     * @param second The second individual to compare.
     * @return An integer indicating the relative fitness of the two individuals.
     */
    def apply(first: Individual[T, F, R], second: Individual[T, F, R]): Int = compare(first, second)

    /** Sorts a population of individuals based on their fitness.
     *
     * This method sorts the population in descending order of fitness, using the built-in `Ordering` instance.
     *
     * @param population The population of individuals to be sorted.
     * @return A sequence of individuals sorted in descending order of fitness.
     */
    def sort(population: Seq[Individual[T, F, R]]): Seq[Individual[T, F, R]] =
        population.sorted(using ordering.reverse)

    /** Transforms a list of fitness values.
     *
     * This method is provided for situations where fitness values need to be transformed before ranking. The default
     * implementation returns the fitness values unchanged, but it can be overridden to apply any necessary
     * transformations.
     *
     * @param fitness The list of fitness values to transform.
     * @return The transformed list of fitness values.
     */
    def fitnessTransform(fitness: List[Double]): List[Double] = fitness
}
