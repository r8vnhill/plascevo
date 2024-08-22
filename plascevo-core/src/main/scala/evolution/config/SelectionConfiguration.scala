package cl.ravenhill.plascevo
package evolution.config

import operators.selection.Selector
import repr.{Feature, Representation}

/** A configuration class for defining the selection process in an evolutionary algorithm.
 *
 * The `SelectionConfiguration` case class encapsulates the configuration needed to manage the selection of individuals
 * during the evolutionary process. It includes parameters for the survival rate, parent selection, and offspring
 * selection.
 *
 * @param survivalRate     The proportion of the population that survives to the next generation. This value should be
 *                         between 0.0 and 1.0.
 * @param parentSelector   The `Selector` used to choose parents from the current population for reproduction.
 * @param offspringSelector The `Selector` used to choose offspring to include in the next generation.
 * @tparam T The type of value stored by the feature.
 * @tparam F The type of feature contained within the representation, which must extend [[Feature]].
 * @tparam R The type of representation used by the individual, which must extend [[Representation]].
 */
case class SelectionConfiguration[T, F <: Feature[T, F], R <: Representation[T, F]](
    survivalRate: Double,
    parentSelector: Selector[T, F, R],
    offspringSelector: Selector[T, F, R]
) {
    require(survivalRate >= 0.0 && survivalRate <= 1.0, "Survival rate must be between 0.0 and 1.0.")
}
