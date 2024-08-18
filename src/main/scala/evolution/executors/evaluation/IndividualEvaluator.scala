package cl.ravenhill.plascevo
package evolution.executors.evaluation

import repr.{Feature, Representation}

/** A class responsible for evaluating the fitness of an individual in an evolutionary algorithm.
 *
 * The `IndividualEvaluator` class evaluates an individual's fitness using a provided fitness function. It maintains the
 * state of the fitness value and provides a method to apply the fitness function to the individual's representation.
 * The class also supports retrieving the evaluated individual with its updated fitness value.
 *
 * @param individual       The individual to be evaluated.
 * @param fitnessFunction  The function used to calculate the fitness of the individual's representation.
 * @tparam T The type of value stored by the feature.
 * @tparam F The type of feature contained within the representation, which must extend [[Feature]].
 * @tparam R The type of representation used by the individual, which must extend [[Representation]].
 */
private[evaluation] class IndividualEvaluator[T, F <: Feature[T, F], R <: Representation[T, F]](
    individual: Individual[T, F, R],
    private val fitnessFunction: R => Double
) {

    /** The current fitness value of the individual, initialized to `NaN` until the evaluation is performed. */
    private var fitness = Double.NaN

    /** A copy of the individual being evaluated, with the fitness value updated after evaluation. */
    private val _individual = individual

    /** Retrieves the individual with the updated fitness value.
     *
     * This method returns a copy of the individual with its fitness value set to the result of the evaluation.
     *
     * @return The individual with the updated fitness value.
     */
    def getIndividual: Individual[T, F, R] = _individual.copy(fitness = fitness)

    /** Applies the fitness function to evaluate the individual's representation.
     *
     * This method calculates the fitness of the individual's representation by applying the provided fitness function.
     * The calculated fitness value is stored and can be accessed via the `getIndividual` method.
     */
    def apply(): Unit = fitness = fitnessFunction(getIndividual.representation)
}
