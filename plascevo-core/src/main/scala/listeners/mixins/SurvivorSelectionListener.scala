package cl.ravenhill.plascevo
package listeners.mixins

import evolution.states.EvolutionState
import repr.{Feature, Representation}

trait SurvivorSelectionListener[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]] {

    def onSurvivorSelectionStart(state: S): Unit = ()

    def onSurvivorSelectionEnd(state: S): Unit = ()
}
