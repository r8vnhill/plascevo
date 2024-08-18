package cl.ravenhill.plascevo
package evolution.executors.evaluation

import repr.{Feature, Representation}
import evolution.states.EvolutionState

/** A sequential evaluator for evaluating individuals in a genetic or evolutionary algorithm.
 *
 * The `SequentialEvaluator` class extends the `EvaluationExecutor` trait and provides a sequential approach to
 * evaluating individuals within the population of an evolutionary state. It applies a specified fitness function to
 * each individual's representation and updates the state with the evaluated population.
 *
 * @param function The fitness function used to evaluate the representations of individuals.
 * @tparam T The type of value stored by the feature.
 * @tparam F The type of feature contained within the representation, which must extend [[Feature]].
 * @tparam R The type of representation used by the individual, which must extend [[Representation]].
 * @tparam S The type of the evolutionary state, which must extend [[EvolutionState]].
 */
class SequentialEvaluator[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]](
    function: R => Double
) extends EvaluationExecutor[T, F, R, S] {

    /** Applies the sequential evaluation process to the population within the evolutionary state.
     *
     * This method evaluates the individuals in the population by applying the provided fitness function. It uses the
     * specified force evaluation strategy to determine which individuals should be evaluated and updates the state with
     * the newly evaluated population.
     *
     * @param state The current evolutionary state, containing the population to be evaluated.
     * @param force The force evaluation strategy, determining which individuals should be evaluated.
     * @return The updated evolutionary state after the evaluation process has been applied.
     */
    override def apply(state: S, force: ForceEvaluation): S = {
        state.withPopulation(
            EvaluationExecutor.evaluateAndAddToPopulation(
                EvaluationExecutor.selectAndCreateEvaluators(state.population, function, force),
                state.population,
                evaluators => evaluators.foreach(evaluator => evaluator())
            )
        )
    }
}
