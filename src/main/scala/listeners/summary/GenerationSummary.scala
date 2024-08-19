package cl.ravenhill.plascevo
package listeners.summary

import repr.{Feature, Representation}

import cl.ravenhill.plascevo.evolution.states.EvolutionState
import cl.ravenhill.plascevo.listeners.mixins.GenerationListener
import cl.ravenhill.plascevo.listeners.records.EvolutionRecord
import cl.ravenhill.plascevo.listeners.{EvolutionListener, ListenerConfiguration}
import cl.ravenhill.plascevo.ranking.IndividualRanker

import scala.concurrent.duration.Duration

trait GenerationSummary[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]](
    configuration: ListenerConfiguration[T, F, R]
) extends GenerationListener[T, F, R, S] {
    private val evolution: EvolutionRecord[T, F, R] = configuration.evolution
    private val ranker: IndividualRanker[T, F, R] = configuration.ranker
    private val precision: Duration => Long = configuration.precision
    private val currentGeneration = configuration.current
}
