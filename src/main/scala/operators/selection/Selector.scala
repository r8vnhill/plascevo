package cl.ravenhill.plascevo
package operators.selection

import evolution.states.EvolutionState
import operators.Operator
import ranking.IndividualRanker
import repr.{Feature, Representation}

import scala.util.Random

/** A trait that represents a selection operator in a genetic or evolutionary algorithm.
 *
 * The `Selector` trait extends the `Operator` trait and defines the behavior for selecting a subset of individuals from
 * a population based on their fitness. Selection operators are essential components in evolutionary algorithms,
 * responsible for choosing the individuals that will participate in the creation of the next generation.
 *
 * @tparam T The type of value stored by the feature.
 * @tparam F The type of feature contained within the representation, which must extend [[Feature]].
 * @tparam R The type of representation used by the individual, which must extend [[Representation]].
 */
trait Selector[T, F <: Feature[T, F], R <: Representation[T, F]] extends Operator[T, F, R] {

    /** Applies the selection operation to modify the population within the evolutionary state.
     *
     * This method selects a subset of individuals from the current population based on their fitness and updates the
     * evolutionary state with the selected individuals. The selection is performed using the `select` method, which
     * must be implemented by subclasses.
     *
     * @param state      The current evolutionary state from which individuals are selected.
     * @param outputSize The number of individuals to be selected.
     * @param buildState A function that takes a sequence of selected individuals and returns a new evolutionary state.
     * @tparam S The type of the evolutionary state, which must extend [[EvolutionState]].
     * @throws IllegalArgumentException if the population is empty or if `outputSize` is non-positive.
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

    /** Selects a subset of individuals from the population based on their fitness.
     *
     * This method must be implemented by subclasses to define the specific selection strategy used to choose
     * individuals from the population. The selection strategy typically involves ranking the individuals based on their
     * fitness and selecting the top individuals to form the next generation.
     *
     * @param population The current population of individuals.
     * @param outputSize The number of individuals to select.
     * @param ranker     The `IndividualRanker` used to evaluate and rank the individuals in the population.
     * @return A sequence of selected individuals to form the next generation.
     */
    def select(
        population: Population[T, F, R],
        outputSize: Int,
        ranker: IndividualRanker[T, F, R]
    )(using random: Random, equalityThreshold: Double): Population[T, F, R]
}
