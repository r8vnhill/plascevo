package cl.ravenhill.plascevo
package listeners

import repr.{Feature, Representation}

import cl.ravenhill.plascevo.listeners.records.EvolutionRecord
import cl.ravenhill.plascevo.ranking.{FitnessMaxRanker, IndividualRanker}

import scala.concurrent.duration.Duration

/** Configuration for a listener that manages evolution and timing within an evolutionary algorithm.
 *
 * The `ListenerConfiguration` case class encapsulates the configuration needed by a listener to manage the evolutionary
 * process. This includes the ranking mechanism, the evolution record, and a precision function for handling durations.
 *
 * @param ranker    The `IndividualRanker` used to evaluate and rank individuals within the evolutionary process.
 * @param evolution The `EvolutionRecord` that tracks the entire sequence of generations and their details.
 * @param precision A function that takes a `Duration` and returns a `Long` value representing the precision
 *                  with which time should be handled or measured.
 * @tparam T The type of value stored by the feature.
 * @tparam F The kind of feature stored in the representation, which must implement [[Feature]].
 * @tparam R The type of representation used by the individual, which must implement [[Representation]].
 */
case class ListenerConfiguration[T, F <: Feature[T, F], R <: Representation[T, F]](
    ranker: IndividualRanker[T, F, R] = FitnessMaxRanker(),
    evolution: EvolutionRecord[T, F, R] = EvolutionRecord[T, F, R](),
    precision: Duration => Long = _.toMillis
)
