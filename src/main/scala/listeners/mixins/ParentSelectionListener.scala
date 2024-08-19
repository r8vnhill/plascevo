package cl.ravenhill.plascevo
package listeners.mixins

import evolution.states.EvolutionState
import repr.{Feature, Representation}

trait ParentSelectionListener[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]] {
    def onParentSelectionStart(state: S): Unit

    def onParentSelectionEnd(state: S): Unit
}
