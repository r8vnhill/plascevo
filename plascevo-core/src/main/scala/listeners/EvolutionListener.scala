package cl.ravenhill.plascevo
package listeners

import evolution.states.EvolutionState
import listeners.mixins.*
import listeners.records.{EvolutionRecord, IndividualRecord}
import ranking.IndividualRanker
import repr.{Feature, Representation}

trait EvolutionListener[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]](
    configuration: ListenerConfiguration[T, F, R]
) extends GenerationListener[T, F, R, S]
  with InitializationListener[T, F, R, S]
  with EvaluationListener[T, F, R, S]
  with ParentSelectionListener[T, F, R, S]
  with SurvivorSelectionListener[T, F, R, S]
  with AlterationListener[T, F, R, S] {

    def fittest: IndividualRecord[T, F, R] = fittestWith(configuration.ranker, configuration.evolution)

    def display = println(toString)

    def onEvolutionStart(state: S): Unit = ()

    def onEvolutionEnd(state: S): Unit = ()
}

object EvolutionListener {
    extension [T](iterable: Iterable[T]) {
        private def maxOfWith[R](ordering: Ordering[R])(selector: T => R): R = {
            val iterator = iterable.iterator
            if (!iterator.hasNext) {
                throw new NoSuchElementException("maxOfWith called on empty iterable")
            }
            var maxValue = selector(iterator.next())
            while (iterator.hasNext) {
                val value = selector(iterator.next())
                if (ordering.gt(value, maxValue)) {
                    maxValue = value
                }
            }
            maxValue
        }
    }

    def computeSteadyGenerations[T, F <: Feature[T, F], R <: Representation[T, F]](
        ranker: IndividualRanker[T, F, R],
        evolution: EvolutionRecord[T, F, R]
    ): Int = {
        var steady = 0
        for (i <- (evolution.generations.size - 1) to 1 by -1) {
            val last = evolution.generations(i - 1)
            val current = evolution.generations(i)

            val lastFittest: Individual[T, F, R] = last.population.offspring
                .filterNot(_.fitness.isNaN)
                .maxOfWith(ranker.ordering)(_.toIndividual())
            val currentFittest: Individual[T, F, R] = current.population.offspring
                .filterNot(_.fitness.isNaN)
                .maxOfWith(ranker.ordering)(_.toIndividual())
            if (lastFittest.fitness == currentFittest.fitness) {
                steady += 1
            } else {
                return steady
            }
        }
        steady
    }
}
