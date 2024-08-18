package cl.ravenhill.plascevo
package operators.alteration

import operators.Operator
import repr.{Feature, Representation}

/** A trait that represents an alterer in an evolutionary algorithm.
 *
 * The `Alterer` trait extends the `Operator` trait and defines the behavior for modifying the population of individuals
 * within an evolutionary process. Alterers can perform various tasks such as mutation, crossover, or other genetic
 * transformations that alter the individuals' representations.
 *
 * @tparam T The type of value stored by the feature.
 * @tparam F The type of feature contained within the representation, which must extend [[Feature]].
 * @tparam R The type of representation used by the individual, which must extend [[Representation]].
 */
trait Alterer[T, F <: Feature[T, F], R <: Representation[T, F]] extends Operator[T, F, R] {

    /** Combines this alterer with another alterer to create a sequence of alterers.
     *
     * This infix method allows two alterers to be combined into a sequence, which can then be applied sequentially
     * during the evolutionary process. The resulting sequence of alterers can be used to perform multiple alterations
     * on the population in a defined order.
     *
     * @param other The other alterer to be combined with this alterer.
     * @return A sequence of alterers combining this alterer and the other alterer.
     */
    infix def +(other: Alterer[T, F, R]): Seq[Alterer[T, F, R]] = Seq(this, other)
}
