package cl.ravenhill.plascevo
package listeners.summary

import evolution.states.EvolutionState
import listeners.ListenerConfiguration
import listeners.mixins.EvaluationListener
import repr.{Feature, Representation}

import scala.concurrent.duration.Deadline

trait EvaluationSummary[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]](
    configuration: ListenerConfiguration[T, F, R]
) extends EvaluationListener[T, F, R, S] {
    private val currentGeneration = configuration.currentGeneration
    private val precision = configuration.precision

    override def onEvaluationStart(state: S): Unit =
        currentGeneration.map(_.evaluation.startTime = Some(Deadline.now))

    override def onEvaluationEnd(state: S): Unit = currentGeneration.map { generation =>
        generation.evaluation.duration =
            precision(Deadline.now - generation.evaluation.startTime.getOrElse(Deadline.now))
    }
}
