package cl.ravenhill.plascevo
package evolution.config

import evolution.states.EvolutionState
import limits.Limit
import listeners.EvolutionListener
import repr.{Feature, Representation}

/** A case class that encapsulates the configuration for an evolutionary algorithm.
 *
 * The `EvolutionConfiguration` class defines the setup for an evolutionary algorithm, including the limits and
 * listeners that govern the evolutionary process. Limits specify conditions under which the evolution should stop,
 * while listeners allow for monitoring and reacting to various events during the evolution.
 *
 * @param limits    A sequence of `Limit` instances that define the stopping conditions for the evolutionary process.
 * @param listeners A sequence of `EvolutionListener` instances that monitor and respond to events during the evolution.
 * @param initialState The initial state of the evolutionary process.
 * @tparam T The type of value stored by the feature.
 * @tparam F The kind of feature stored in the representation, which must implement [[Feature]].
 * @tparam R The type of representation used by the individual, which must implement [[Representation]].
 * @tparam S The type of the evolutionary state, which must extend [[EvolutionState]].
 */
case class EvolutionConfiguration[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R]](
    limits: Seq[Limit[T, F, R, S, ?]],
    listeners: Seq[EvolutionListener[T, F, R, S]],
    initialState: S
)
