package cl.ravenhill.plascevo
package limits

import repr.{Feature, Representation}

import cl.ravenhill.plascevo.evolution.states.EvolutionState
import cl.ravenhill.plascevo.listeners.EvolutionListener
import cl.ravenhill.plascevo.utils.===

class TargetFitness[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]](
    predicate: Double => Boolean
) extends Limit[T, F, R, S, EvolutionListener[T, F, R, S]](
    TargetFitnessListener(),
    (_, state) => state.population.exists(individual => predicate(individual.fitness))
) {
    def this(targetFitness: Double)(using equalityThreshold: Double) = this(_ === targetFitness)
}

private class TargetFitnessListener[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]]
    extends EvolutionListener[T, F, R, S]
