package cl.ravenhill.plascevo

import listeners.records.{EvolutionRecord, IndividualRecord}
import ranking.IndividualRanker
import repr.{Feature, Representation}

package object listeners {
    private[listeners] def fittestWith[T, F <: Feature[T, F], R <: Representation[T, F]](
        ranker: IndividualRanker[T, F, R], evolution: EvolutionRecord[T, F, R]
    ): IndividualRecord[T, F, R] = {
        IndividualRecord.fromIndividual(
            ranker.sort(
                evolution.generations
                    .last.population
                    .offspring
                    .map(_.toIndividual())
            ).head
        )
    }
}
