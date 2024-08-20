package cl.ravenhill.plascevo
package repr

import scala.util.Random

/** A trait that represents a factory for creating representations in an evolutionary algorithm.
 *
 * The `RepresentationFactory` trait defines a factory interface for creating instances of a specific type of
 * representation. This allows for the generation of new representations, which can be used to initialize or modify
 * populations in evolutionary algorithms.
 *
 * @tparam T The type of value stored by the feature.
 * @tparam F The type of feature contained within the representation, which must extend [[Feature]].
 * @tparam R The type of representation to be created, which must extend [[Representation]].
 */
trait RepresentationFactory[T, F <: Feature[T, F], R <: Representation[T, F]] {

    /** Creates a new instance of the representation.
     *
     * This method is responsible for generating a new representation, typically used to create individuals
     * or populations within an evolutionary algorithm. The specifics of how the representation is created
     * are defined by the implementing class.
     *
     * @return A new instance of the representation.
     */
    def build()(using random: Random): R
}
