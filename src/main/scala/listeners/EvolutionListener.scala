package cl.ravenhill.plascevo
package listeners

import evolution.states.EvolutionState
import listeners.mixins.GenerationListener
import repr.{Feature, Representation}

trait EvolutionListener[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R]]
    extends GenerationListener[T, F, R, S]:
    def onEvolutionStart(state: S): Unit = ()

    def onEvolutionEnd(state: S): Unit = ()
