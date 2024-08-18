package cl.ravenhill.plascevo
package evolution.config

import evolution.EvolutionInterceptor
import evolution.executors.evaluation.EvaluationExecutor
import evolution.states.EvolutionState
import limits.Limit
import listeners.EvolutionListener
import ranking.IndividualRanker
import repr.{Feature, Representation}

/** A configuration class for defining the parameters and components of an evolutionary algorithm.
 *
 * The `EvolutionConfiguration` case class encapsulates all the essential components and parameters required to
 * configure and run an evolutionary algorithm. This includes the limits that define stopping conditions, listeners that
 * react to various events during evolution, a ranker for evaluating individuals, an evaluator for assessing the
 * population, an interceptor for modifying the evolutionary state, and the initial state of the evolution.
 *
 * @param limits       A sequence of `Limit` instances that define stopping conditions for the evolutionary process.
 * @param listeners    A sequence of `EvolutionListener` instances that react to events during the evolutionary process,
 *                     such as evolution start and end.
 * @param ranker       An `IndividualRanker` used to evaluate and compare individuals within the population.
 * @param evaluator    An `EvaluationExecutor` responsible for evaluating the fitness of individuals in the population.
 * @param interceptor  An `EvolutionInterceptor` used to modify the evolutionary state before and after evolution steps.
 * @param initialState The initial state of the evolutionary process, representing the starting population and conditions.
 * @tparam T The type of value stored by the feature.
 * @tparam F The type of feature contained within the representation, which must extend [[Feature]].
 * @tparam R The type of representation used by the individual, which must extend [[Representation]].
 * @tparam S The type of the evolutionary state, which must extend [[EvolutionState]].
 */
case class EvolutionConfiguration[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]](
    limits: Seq[Limit[T, F, R, S, ?]],
    listeners: Seq[EvolutionListener[T, F, R, S]],
    ranker: IndividualRanker[T, F, R],
    evaluator: EvaluationExecutor[T, F, R, S],
    interceptor: EvolutionInterceptor[T, F, R, S],
    initialState: S
)
