package cl.ravenhill.plascevo
package listeners

import evolution.states.EvolutionState
import listeners.mixins.GenerationListener
import listeners.records.EvolutionRecord
import ranking.IndividualRanker
import repr.{Feature, Representation}

/** A trait that provides hooks for listening to events during the evolution process in a genetic or evolutionary
 * algorithm.
 *
 * The `EvolutionListener` trait extends the `GenerationListener` trait and defines additional methods that can be
 * implemented to perform actions at the start and end of the entire evolutionary process. This allows for custom
 * behaviors or logging to be injected into the lifecycle of the algorithm.
 *
 * @tparam T The type of value stored by the feature.
 * @tparam F The type of feature contained within the representation, which must extend [[Feature]].
 * @tparam R The type of representation used by the individual, which must extend [[Representation]].
 * @tparam S The type of the evolutionary state, which must extend [[EvolutionState]].
 */
trait EvolutionListener[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]]
    extends GenerationListener[T, F, R, S] {

    /** Called at the start of the evolutionary process.
     *
     * This method is a hook that is invoked at the beginning of the entire evolutionary process. Subclasses can
     * override this method to perform custom actions such as initializing resources, logging, or setting up the initial
     * conditions before the generations are processed.
     *
     * @param state The current state of the evolutionary process at the start.
     */
    def onEvolutionStart(state: S): Unit = ()

    /** Called at the end of the evolutionary process.
     *
     * This method is a hook that is invoked at the end of the entire evolutionary process. Subclasses can override this
     * method to perform custom actions such as cleaning up resources, logging results, or performing final evaluations
     * after all generations have been processed.
     *
     * @param state The current state of the evolutionary process at the end.
     */
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
