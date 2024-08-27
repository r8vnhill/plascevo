package cl.ravenhill.plascevo
package evolution.executors.evaluation

import cl.ravenhill.plascevo.evolution.states.EvolutionState
import cl.ravenhill.plascevo.repr.{Feature, Representation}

/** A factory class for creating instances of `EvaluationExecutor` in a genetic or evolutionary algorithm.
 *
 * The `EvaluationExecutorFactory` class provides a mechanism for creating instances of
 * `EvaluationExecutor[T, F, R, S]`. It allows the creation strategy to be customized by assigning a different `creator`
 * function, which returns a specific implementation of `EvaluationExecutor[T, F, R, S]`. By default, it uses the
 * `SequentialEvaluator` to create instances.
 *
 * @tparam T The type of value stored by the feature.
 * @tparam F The type of feature contained within the representation, which must extend [[Feature]].
 * @tparam R The type of representation used by the individual, which must extend [[Representation]].
 * @tparam S The type of the evolutionary state, which must extend [[EvolutionState]].
 */
class EvaluationExecutorFactory[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]] {
    
    /** A function that creates instances of `EvaluationExecutor[T, F, R, S]`.
     *
     * This variable holds a function that returns an instance of `EvaluationExecutor[T, F, R, S]` based on the provided
     * fitness function. By default, it creates instances of `SequentialEvaluator[T, F, R, S]`. The function can be
     * reassigned to provide different creation strategies.
     */
    var creator: (R => Double) => EvaluationExecutor[T, F, R, S] = { function =>
        SequentialEvaluator[T, F, R, S](function)
    }
}
