package cl.ravenhill.plascevo
package evolution.executors.evaluation

import evolution.executors.evaluation.ForceEvaluation.{All, New, None}
import evolution.states.EvolutionState
import repr.{Feature, Representation}

/** A trait that defines the execution of the evaluation process in a genetic or evolutionary algorithm.
 *
 * The `EvaluationExecutor` trait provides an interface for applying evaluation strategies to the population within an
 * evolutionary state. It defines a method for executing the evaluation based on the current state and a specified force
 * evaluation strategy.
 *
 * @tparam T The type of value stored by the feature.
 * @tparam F The type of feature contained within the representation, which must extend [[Feature]].
 * @tparam R The type of representation used by the individual, which must extend [[Representation]].
 * @tparam S The type of the evolutionary state, which must extend [[EvolutionState]].
 */
trait EvaluationExecutor[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]] {

    /** Executes the evaluation process on the current state of the evolutionary algorithm.
     *
     * This method applies the evaluation strategy to the population within the given state, using the specified force
     * evaluation strategy to determine which individuals should be evaluated.
     *
     * @param state The current evolutionary state, containing the population to be evaluated.
     * @param force The force evaluation strategy, determining which individuals should be evaluated.
     * @return The updated evolutionary state after the evaluation process has been applied.
     */
    def apply(state: S, force: ForceEvaluation = ForceEvaluation.New): S
}

/** Companion object for the `EvaluationExecutor` trait, providing utility methods for evaluation.
 *
 * The `EvaluationExecutor` object includes private methods that are used internally to select and create evaluators for
 * individuals, as well as to perform the evaluation and update the population accordingly.
 */
object EvaluationExecutor {

    /** Selects individuals from the population and creates their evaluators based on the force evaluation strategy.
     *
     * This method filters the population according to the provided `ForceEvaluation` strategy and creates a sequence of
     * `IndividualEvaluator` instances for the selected individuals.
     *
     * @param population The population of individuals to be considered for evaluation.
     * @param function   The fitness function used to evaluate the individuals' representations.
     * @param force      The force evaluation strategy, determining which individuals should be evaluated.
     * @return A sequence of `IndividualEvaluator` instances for the selected individuals.
     */
    private[evaluation] def selectAndCreateEvaluators[T, F <: Feature[T, F], R <: Representation[T, F]](
        population: Population[T, F, R],
        function: R => Double,
        force: ForceEvaluation
    ): Seq[IndividualEvaluator[T, F, R]] = {
        val individuals = force match {
            case All => population
            case New => population.filterNot(_.isEvaluated)
            case None => Seq()
        }
        individuals.map(individual => IndividualEvaluator(individual, function))
    }

    /** Evaluates the selected individuals and updates the population with the evaluated individuals.
     *
     * This method applies the evaluation strategy to the selected individuals and updates the population by replacing
     * the unevaluated individuals with their evaluated counterparts.
     *
     * @param toEvaluate         The sequence of `IndividualEvaluator` instances for the individuals to be evaluated.
     * @param population         The current population of individuals in the evolutionary process.
     * @param evaluationStrategy The strategy to be used for evaluating the selected individuals.
     * @return The updated population after the evaluation process.
     */
    private[evaluation] def evaluateAndAddToPopulation[T, F <: Feature[T, F], R <: Representation[T, F]](
        toEvaluate: Seq[IndividualEvaluator[T, F, R]],
        population: Population[T, F, R],
        evaluationStrategy: Seq[IndividualEvaluator[T, F, R]] => Unit
    ): Population[T, F, R] = {
        if (toEvaluate.nonEmpty) {
            evaluationStrategy(toEvaluate)
            if (toEvaluate.size == population.size) {
                toEvaluate.map(_.getIndividual)
            } else {
                population.filter(_.isEvaluated) ++ toEvaluate.map(_.getIndividual)
            }
        } else {
            population
        }
    }
}
