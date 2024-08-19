package cl.ravenhill.plascevo
package listeners

import repr.{Feature, Representation}

import cl.ravenhill.plascevo.listeners.records.{EvolutionRecord, GenerationRecord}
import cl.ravenhill.plascevo.ranking.{FitnessMaxRanker, IndividualRanker}
import cl.ravenhill.plascevo.utils.Box

import scala.concurrent.duration.Duration

case class ListenerConfiguration[T, F <: Feature[T, F], R <: Representation[T, F]](
    ranker: IndividualRanker[T, F, R] = FitnessMaxRanker(),
    evolution: EvolutionRecord[T, F, R] = EvolutionRecord[T, F, R](),
    precision: Duration => Long = _.toMillis
) {
    val currentGeneration = Box.mutable[GenerationRecord[T, F, R]](None)
}
