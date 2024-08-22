package cl.ravenhill.plascevo
package listeners.summary

import evolution.states.EvolutionState
import listeners.ListenerConfiguration
import listeners.mixins.SurvivorSelectionListener
import repr.{Feature, Representation}

import scala.concurrent.duration.Deadline

trait SurvivorSelectionSummary[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]](
    configuration: ListenerConfiguration[T, F, R]
) extends SurvivorSelectionListener[T, F, R, S] {

    private val currentGeneration = configuration.currentGeneration

    private val precision = configuration.precision

    override def onSurvivorSelectionStart(state: S): Unit = currentGeneration.map { generation =>
        generation.survivorSelection.startTime = Some(Deadline.now)
    }

    override def onSurvivorSelectionEnd(state: S): Unit = currentGeneration.map { generation =>
        generation.survivorSelection.duration =
            precision(Deadline.now - generation.survivorSelection.startTime.getOrElse(Deadline.now))
    }
}
