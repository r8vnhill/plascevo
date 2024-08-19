package cl.ravenhill.plascevo
package listeners.mixins

import repr.{Feature, Representation}

import cl.ravenhill.plascevo.evolution.states.EvolutionState
import cl.ravenhill.plascevo.listeners.ListenerConfiguration

trait EvaluationListener[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]] {
    
    def onEvaluationStart(state: S): Unit = ()
    
    def onEvaluationEnd(state: S): Unit = ()
}
