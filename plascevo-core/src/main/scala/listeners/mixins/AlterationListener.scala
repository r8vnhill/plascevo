package cl.ravenhill.plascevo
package listeners.mixins

import repr.{Feature, Representation}

import cl.ravenhill.plascevo.evolution.states.EvolutionState

trait AlterationListener[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]] {

    def onAlterationStart(state: S): Unit = ()

    def onAlterationEnd(state: S): Unit = ()
}
