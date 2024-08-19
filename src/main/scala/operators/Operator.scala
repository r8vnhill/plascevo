package cl.ravenhill.plascevo
package operators

import evolution.states.EvolutionState
import repr.{Feature, Representation}

import scala.util.Random

/** A trait that represents an operator in a genetic or evolutionary algorithm.
 *
 * The `Operator` trait defines the behavior of an operator that modifies the population of individuals within an
 * evolutionary state. Operators can perform various tasks such as selection, crossover, mutation, or other genetic
 * transformations that alter the population during the evolutionary process.
 *
 * @tparam T The type of value stored by the feature.
 * @tparam F The type of feature contained within the representation, which must extend [[Feature]].
 * @tparam R The type of representation used by the individual, which must extend [[Representation]].
 */
trait Operator[T, F <: Feature[T, F], R <: Representation[T, F]] {

    /** Applies the operator to modify the population within the evolutionary state.
     *
     * This method applies the operator to the current state, producing a modified population of individuals. The
     * operator can create a new population of the specified size and update the state using the provided `buildState`
     * function.
     *
     * @param state      The current evolutionary state to which the operator is applied.
     * @param outputSize The number of individuals to be produced by the operator.
     * @param buildState A function that takes a sequence of individuals and returns a new evolutionary state.
     * @tparam S The type of the evolutionary state, which must extend [[EvolutionState]].
     */
    def apply[S <: EvolutionState[T, F, R, S]](
        state: S,
        outputSize: Int,
        buildState: Seq[Individual[T, F, R]] => S
    )(using random: Random): S
}
