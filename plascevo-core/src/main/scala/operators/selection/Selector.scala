package cl.ravenhill.plascevo
package operators.selection

import evolution.states.EvolutionState
import operators.Operator
import ranking.IndividualRanker
import repr.{Feature, Representation}

import scala.util.Random

/** Represents a selector in an evolutionary algorithm.
 *
 * A `Selector` is a specialized operator used in evolutionary algorithms to select individuals from a population based
 * on a ranker criteria. This selection process is crucial for guiding the evolutionary process by promoting the
 * reproduction of fitter individuals. The `Selector` trait extends the [[Operator]] trait and provides an
 * implementation of the `apply` method, which handles the selection of individuals and ensures that the selected
 * population matches the specified output size.
 *
 * @tparam T The type of value stored by the features within the individual.
 * @tparam F The type of feature stored in the representation, which must extend [[Feature]].
 * @tparam R The type of representation used by the individual, which must extend [[Representation]].
 *
 * @example
 * {{{
 * // Example implementation of a simple selector that picks individuals based on their fitness
 * class SimpleSelector extends Selector[Int, SimpleGene, SimpleRepresentation] {
 *   override def select(
 *     population: Population[Int, SimpleGene, SimpleRepresentation],
 *     outputSize: Int,
 *     ranker: IndividualRanker[Int, SimpleGene, SimpleRepresentation]
 *   )(using random: Random, equalityThreshold: Double): Population[Int, SimpleGene, SimpleRepresentation] = {
 *     // Select the top individuals based on their fitness
 *     ranker.sort(population).take(outputSize)
 *   }
 * }
 * }}}
 */
trait Selector[T, F <: Feature[T, F], R <: Representation[T, F]] extends Operator[T, F, R] {

    /** Applies the selector to the current evolutionary state, producing a new state with a selected population.
     *
     * The `apply` method is responsible for selecting individuals from the current population based on their fitness
     * and the selection strategy defined in the `select` method. It ensures that the population is not empty, the
     * output size is positive, and that the selected population size matches the expected output size.
     *
     * @param state      The current evolutionary state containing the population of individuals.
     * @param outputSize The number of individuals to select for the resulting state.
     * @param buildState A function that constructs the new evolutionary state from the selected individuals.
     * @param random An implicit random number generator used for stochastic operations.
     * @param equalityThreshold An implicit threshold for determining equality between features.
     * @tparam S The type of the evolutionary state, which must extend [[EvolutionState]].
     * @return The new evolutionary state after applying the selection.
     * @throws IllegalArgumentException if the population is empty or the output size is non-positive.
     */
    override def apply[S <: EvolutionState[T, F, R, S]](
        state: S,
        outputSize: Int,
        buildState: Seq[Individual[T, F, R]] => S
    )(using random: Random, equalityThreshold: Double): S = {
        require(!state.isEmpty, "Cannot select individuals from an empty population.")
        require(outputSize > 0, "Cannot select a non-positive number of individuals.")

        val selectedPopulation = select(state.population, outputSize, state.ranker)
        val newState = buildState(selectedPopulation)
        require(newState.size == outputSize, "Selected population size does not match the expected output size.")
        newState
    }

    /** Selects individuals from the population based on their fitness and a given selection strategy.
     *
     * The `select` method is an abstract method that must be implemented by subclasses to define the specific
     * selection strategy. This method is responsible for choosing a subset of individuals from the population
     * based on a ranker criteria, such as fitness. The selected individuals are then returned as a new population.
     *
     * @param population The current population of individuals to select from.
     * @param outputSize The number of individuals to select.
     * @param ranker The ranker used to evaluate and compare the fitness of individuals.
     * @param random An implicit random number generator used for stochastic operations.
     * @param equalityThreshold An implicit threshold for determining equality between features.
     * @return A selected population of individuals.
     */
    def select(
        population: Population[T, F, R],
        outputSize: Int,
        ranker: IndividualRanker[T, F, R]
    )(using random: Random, equalityThreshold: Double): Population[T, F, R]
}
