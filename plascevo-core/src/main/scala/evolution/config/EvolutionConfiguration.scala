package cl.ravenhill.plascevo
package evolution.config

import evolution.EvolutionInterceptor
import evolution.executors.evaluation.EvaluationExecutor
import evolution.states.EvolutionState
import limits.Limit
import listeners.EvolutionListener
import ranking.IndividualRanker
import repr.{Feature, Representation}

/** Configuration for an evolutionary algorithm's setup and execution.
 *
 * The `EvolutionConfiguration` class encapsulates the configuration necessary to run an evolutionary algorithm. It
 * includes the limits that determine when the evolution process should stop, listeners that respond to various events
 * during the evolution, the ranker used to evaluate and compare individuals, the evaluator that performs fitness
 * evaluations, an interceptor for customizing the evolution process, and the initial state from which the evolution
 * begins.
 *
 * @param limits A sequence of limits that dictate when the evolutionary process should terminate. Each limit is
 *               associated with a specific type of listener.
 * @param listeners A sequence of listeners that react to events occurring during the evolution, such as generation
 *                  ends or state changes.
 * @param ranker The ranker used to compare individuals based on their fitness, guiding the selection process.
 * @param evaluator The executor responsible for evaluating the fitness of individuals within the population.
 * @param interceptor An interceptor that allows for the customization of the evolutionary process, such as modifying
 *                    the state or influencing selection.
 * @param initialState The initial state of the evolutionary process, serving as the starting point for the algorithm.
 *
 * @tparam T The type of value stored by the features within the representation.
 * @tparam F The type of feature stored in the representation, which must extend [[Feature]].
 * @tparam R The type of representation used by the individuals in the population, which must extend [[Representation]].
 * @tparam S The type of the evolutionary state, which must extend [[EvolutionState]].
 * @tparam L The type of listener used in conjunction with the limits, which must extend [[EvolutionListener]].
 *
 * @example
 * {{{
 * // Example configuration for a simple evolutionary algorithm
 * val config = EvolutionConfiguration(
 *     limits = Seq(MaxGenerations(100)),
 *     listeners = Seq(EvolutionSummary()),
 *     ranker = FitnessMaxRanker(),
 *     evaluator = SequentialEvaluationExecutor(),
 *     interceptor = DefaultEvolutionInterceptor(),
 *     initialState = GeneticEvolutionState.empty
 * )
 * // The algorithm is now configured to run for up to 100 generations, evaluating fitness and using the provided
 * // listeners.
 * }}}
 */
case class EvolutionConfiguration[
    T,
    F <: Feature[T, F],
    R <: Representation[T, F],
    S <: EvolutionState[T, F, R, S],
    L <: EvolutionListener[T, F, R, S]
](
    limits: Seq[Limit[T, F, R, S, L]],
    listeners: Seq[L],
    ranker: IndividualRanker[T, F, R],
    evaluator: EvaluationExecutor[T, F, R, S],
    interceptor: EvolutionInterceptor[T, F, R, S],
    initialState: S
)
