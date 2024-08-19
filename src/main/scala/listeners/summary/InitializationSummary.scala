package cl.ravenhill.plascevo
package listeners.summary

import evolution.states.EvolutionState
import listeners.ListenerConfiguration
import listeners.mixins.InitializationListener
import repr.{Feature, Representation}

import scala.concurrent.duration.Deadline

trait InitializationSummary[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]](
    configuration: ListenerConfiguration[T, F, R]
) extends InitializationListener[T, F, R, S] {
    private val evolution = configuration.evolution

    private val precision = configuration.precision

    override def onInitializationStart(state: S): Unit = evolution.initialization.startTime = Some(Deadline.now)

    override def onInitializationEnd(state: S): Unit = {
        evolution.initialization.duration =
            precision(Deadline.now - evolution.initialization.startTime.getOrElse(Deadline.now))
    }
}
