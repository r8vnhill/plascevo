package cl.ravenhill.plascevo
package listeners.mixins

import evolution.states.EvolutionState
import listeners.ListenerConfiguration
import repr.{Feature, Representation}

trait InitializationListener[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]] {
    val configuration: ListenerConfiguration[T, F, R]
    
    def onInitializationStart(state: S): Unit = ()

    def onInitializationEnd(state: S): Unit = ()
}
