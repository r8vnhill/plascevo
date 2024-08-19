package cl.ravenhill.plascevo
package listeners.summary

import evolution.states.EvolutionState
import repr.{Feature, Representation}

import cl.ravenhill.plascevo.listeners.ListenerConfiguration
import cl.ravenhill.plascevo.listeners.mixins.ParentSelectionListener

import scala.concurrent.duration.Deadline

trait ParentSelectionSummary[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]](
    configuration: ListenerConfiguration[T, F, R]
) extends ParentSelectionListener[T, F, R, S] {

    private val currentGeneration = configuration.currentGeneration

    private val precision = configuration.precision

    override def onParentSelectionStart(state: S): Unit = currentGeneration.map { generation =>
        generation.parentSelection.startTime = Some(Deadline.now)
    }

    override def onParentSelectionEnd(state: S): Unit = currentGeneration.map { generation =>
        generation.parentSelection.duration =
            precision(Deadline.now - generation.parentSelection.startTime.getOrElse(Deadline.now))
    }
}
