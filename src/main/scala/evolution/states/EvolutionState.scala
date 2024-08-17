package cl.ravenhill.plascevo
package evolution.states

import mixins.{FlatMappable, Foldable}
import ranking.IndividualRanker
import repr.{Feature, Representation}

/** Represents the state of an evolutionary algorithm at a given generation.
 *
 * The `EvolutionState` trait encapsulates the population of individuals, the generation number, and the ranking
 * mechanism. It provides functionality to map over the population, fold the population's features, and flatten the
 * population into a list of features. This trait is a foundational component in evolutionary algorithms, allowing for
 * state transitions and manipulations.
 *
 * @tparam T The type of value stored by the feature.
 * @tparam F The kind of feature stored in the representation, which must implement [[Feature]].
 * @tparam R The type of representation used by the individual, which must implement [[Representation]].
 */
trait EvolutionState[T, F <: Feature[T, F], R <: Representation[T, F]] extends FlatMappable[T] with Foldable[T]:

    /** The population of individuals in the current state.
     *
     * This is the collection of all individuals in the current generation of the evolutionary algorithm.
     */
    val population: Population[T, F, R]

    /** The number of individuals in the population.
     *
     * This value is lazily evaluated and corresponds to the size of the current population.
     */
    lazy val size: Int = population.size

    /** The mechanism used to rank individuals based on their fitness.
     *
     * The ranker is used to compare and sort individuals within the population.
     */
    val ranker: IndividualRanker[T, F, R]

    /** The generation number of the evolutionary process.
     *
     * This value represents the current generation within the evolutionary algorithm.
     */
    val generation: Int

    /** Checks if the population is empty.
     *
     * @return `true` if the population is empty, `false` otherwise.
     */
    def isEmpty: Boolean = population.isEmpty

    /** Applies a transformation function to each individual in the population.
     *
     * This method maps over the population, applying the given function to each individual, and returns a new
     * `EvolutionState` with the transformed population.
     *
     * @param f The function to apply to each individual.
     * @return A new `EvolutionState` with the transformed population.
     */
    def map(f: Individual[T, F, R] => Individual[T, F, R]): EvolutionState[T, F, R] =
        withPopulation(population.map(f))

    /** Folds the features of the population from the right.
     *
     * This method folds the features of all individuals in the population from right to left, using the provided
     * binary operation.
     *
     * @param initial The initial value to start the folding operation.
     * @param f       A binary operation that takes the next feature and the current accumulated value, and returns the new accumulated value.
     * @tparam U      The type of the accumulated value and the result.
     * @return The result of folding the features of the population from the right.
     */
    override def foldRight[U](initial: U)(f: (T, U) => U): U =
        population.foldRight(initial)((ind, acc) => ind.representation.foldRight(acc)(f))

    /** Folds the features of the population from the left.
     *
     * This method folds the features of all individuals in the population from left to right, using the provided
     * binary operation.
     *
     * @param initial The initial value to start the folding operation.
     * @param f       A binary operation that takes the current accumulated value and the next feature, and returns the
     *                new accumulated value.
     * @tparam U      The type of the accumulated value and the result.
     * @return The result of folding the features of the population from the left.
     */
    override def foldLeft[U](initial: U)(f: (U, T) => U): U =
        population.foldLeft(initial)((acc, ind) => ind.representation.foldLeft(acc)(f))

    /** Flattens the features of all individuals in the population into a list.
     *
     * This method returns a list containing all features of all individuals in the population.
     *
     * @return A list of all features in the population.
     */
    override def flatten(): Seq[T] = population.flatMap(_.representation.flatten())

    /** Creates a new `EvolutionState` with a given population.
     *
     * This method returns a new state of the evolutionary process, where the population is replaced with the provided
     * one.
     *
     * @param newPopulation The new population to be used in the state.
     * @return A new `EvolutionState` with the updated population.
     */
    def withPopulation(newPopulation: Population[T, F, R]): EvolutionState[T, F, R]
