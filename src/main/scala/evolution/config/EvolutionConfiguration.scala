package cl.ravenhill.plascevo
package evolution.config

import evolution.EvolutionInterceptor
import evolution.executors.evaluation.EvaluationExecutor
import evolution.states.EvolutionState
import limits.Limit
import listeners.EvolutionListener
import ranking.IndividualRanker
import repr.{Feature, Representation}

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
