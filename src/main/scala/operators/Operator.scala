package cl.ravenhill.plascevo
package operators

import evolution.states.EvolutionState
import repr.{Feature, Representation}

import scala.util.Random

/**
 * Defines a trait for an operator used in evolutionary algorithms. An operator performs a transformation on a
 * population of individuals, resulting in a new state of evolution. This trait is typically implemented by classes that
 * perform operations such as crossover, mutation, or selection within an evolutionary algorithm.
 *
 * @tparam T The type of value stored by the feature.
 * @tparam F The type of feature stored in a representation, which must implement [[Feature]].
 * @tparam R The type of representation used by the individual, which must implement [[Representation]].
 */
trait Operator[T, F <: Feature[T, F], R <: Representation[T, F]] {

    /**
     * Applies the operator to the current evolutionary state, producing a new state.
     *
     * @param state The current evolutionary state containing the population of individuals.
     * @param outputSize The number of individuals to produce in the resulting state.
     * @param buildState A function that constructs the new evolutionary state from a sequence of individuals.
     * @param random An implicit random number generator used for stochastic operations.
     * @param equalityThreshold An implicit threshold for determining equality between features.
     * @tparam S The type of the evolutionary state, which must extend [[EvolutionState]].
     * @return The new evolutionary state after applying the operator.
     */
    def apply[S <: EvolutionState[T, F, R, S]](
        state: S,
        outputSize: Int,
        buildState: Seq[Individual[T, F, R]] => S
    )(using random: Random, equalityThreshold: Double): S
}
