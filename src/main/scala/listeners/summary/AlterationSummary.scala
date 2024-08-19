package cl.ravenhill.plascevo
package listeners.summary

import evolution.states.EvolutionState
import listeners.ListenerConfiguration
import listeners.mixins.AlterationListener
import repr.{Feature, Representation}

import scala.concurrent.duration.Deadline

trait AlterationSummary[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]](
    configuration: ListenerConfiguration[T, F, R]
) extends AlterationListener[T, F, R, S] {

    private val currentGeneration = configuration.currentGeneration

    private val precision = configuration.precision

    override def onAlterationStart(state: S): Unit = currentGeneration.map { generation =>
        generation.alteration.startTime = Some(Deadline.now)
    }

    override def onAlterationEnd(state: S): Unit = currentGeneration.map { generation =>
        generation.alteration.duration =
            precision(Deadline.now - generation.alteration.startTime.getOrElse(Deadline.now))
    }
}
