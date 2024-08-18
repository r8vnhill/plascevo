package cl.ravenhill.plascevo
package evolution.config

import operators.alteration.Alterer
import repr.{Feature, Representation}

/** A configuration class for defining the alteration process in a genetic or evolutionary algorithm.
 *
 * The `AlterationConfiguration` case class encapsulates the configuration needed to manage the alteration of
 * individuals within the population during the evolutionary process. This configuration includes a sequence of
 * `Alterer` instances that define the specific alterations (such as mutation, crossover, etc.) to be applied to the
 * population.
 *
 * @param alterers A sequence of `Alterer` instances that define the alterations to be applied to the population.
 * @tparam T The type of value stored by the feature.
 * @tparam F The type of feature contained within the representation, which must extend [[Feature]].
 * @tparam R The type of representation used by the individual, which must extend [[Representation]].
 */
case class AlterationConfiguration[T, F <: Feature[T, F], R <: Representation[T, F]](
    alterers: Seq[Alterer[T, F, R]]
)
