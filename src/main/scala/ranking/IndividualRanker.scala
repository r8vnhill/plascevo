package cl.ravenhill.plascevo
package ranking

import repr.{Feature, Representation}

/** A trait for ranking and comparing individuals in an evolutionary algorithm based on their fitness.
 *
 * The `IndividualRanker` trait provides functionality to compare, sort, and transform individuals and their fitness
 * values within a population. It includes an implicit ordering for individuals based on their fitness, allowing for
 * easy integration with sorting operations.
 *
 * @tparam T The type of value stored by the feature.
 * @tparam F The kind of feature stored in the representation, which must implement [[Feature]].
 * @tparam R The type of representation used by the individual, which must implement [[Representation]].
 */
trait IndividualRanker[T, F <: Feature[T, F], R <: Representation[T, F]]:

    /** The ordering used for comparing two individuals based on their fitness.
     *
     * This implicit ordering defines how individuals should be compared and ordered within a population,
     * based on their fitness values.
     */
    given ordering: Ordering[Individual[T, F, R]] with
        def compare(first: Individual[T, F, R], second: Individual[T, F, R]): Int =
            IndividualRanker.this.compare(first, second)

    /** Compares two individuals based on their fitness.
     *
     * This method compares the fitness values of two individuals, returning a negative integer if the first
     * individual is less fit, zero if they are equally fit, or a positive integer if the first individual is more fit.
     *
     * @param first  The first individual to compare.
     * @param second The second individual to compare.
     * @return A negative integer, zero, or a positive integer if the first individual's fitness is less than, equal to,
     *         or greater than the second individual's fitness, respectively.
     */
    def compare(first: Individual[T, F, R], second: Individual[T, F, R]): Int

    /** Compares two individuals by applying the comparison function.
     *
     * This method is a shorthand for invoking the `compare` method directly. It allows for using the `IndividualRanker`
     * in a functional style by providing an `apply` method, which compares two individuals based on their fitness.
     *
     * @param first  The first individual to compare.
     * @param second The second individual to compare.
     * @return A negative integer, zero, or a positive integer if the first individual's fitness is less than, equal to,
     *         or greater than the second individual's fitness, respectively.
     */
    def apply(first: Individual[T, F, R], second: Individual[T, F, R]): Int = compare(first, second)


/** Sorts a population based on the fitness values of the individuals.
     *
     * This method sorts the given population of individuals in descending order of their fitness values,
     * using the defined ordering.
     *
     * @param population The population to sort.
     * @return A sorted list of individuals in descending order of their fitness values.
     */
    def sort(population: Seq[Individual[T, F, R]]): Seq[Individual[T, F, R]] =
        population.sorted(using ordering.reverse)

    /** Transforms a list of fitness values.
     *
     * This method allows for custom transformations to be applied to a list of fitness values.
     * By default, it returns the fitness values unmodified.
     *
     * @param fitness The list of fitness values to transform.
     * @return The transformed list of fitness values.
     */
    def fitnessTransform(fitness: List[Double]): List[Double] = fitness
