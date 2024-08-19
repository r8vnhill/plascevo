package cl.ravenhill.plascevo
package limits

import evolution.states.EvolutionState
import listeners.EvolutionListener
import repr.{Feature, Representation}

case class MaxGenerations[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]](
    maxGenerations: Int
) extends Limit[T, F, R, S, EvolutionListener[T, F, R, S]](
    MaxGenerationsListener(),
    (_, state) => state.generation >= maxGenerations
)

private class MaxGenerationsListener[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]]
    extends EvolutionListener[T, F, R, S]
