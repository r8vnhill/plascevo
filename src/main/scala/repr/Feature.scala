package cl.ravenhill.plascevo
package repr

import mixins.{FlatMappable, Foldable, Verifiable}

/** Represents a feature in an evolutionary algorithm.
 *
 * A feature is an atomic component in an evolutionary algorithm. For instance, a gene is a feature in a genetic
 * algorithm, and a chromosome is a vector of such features. The `Feature` trait provides a common structure for these
 * components, with methods for duplicating features with new values.
 *
 * <h2>Usage:</h2>
 * This trait is intended to be implemented by classes representing individual elements in evolutionary algorithms, such
 * as genes or other units.
 *
 * <h3>Example 1: Implementing Feature</h3>
 *
 * @example
 * {{{
 * class MyGene(val value: Int) extends Feature[Int, MyGene] {
 *   override def duplicateWithValue(value: Int): MyGene = new MyGene(value)
 * }
 * }}}
 * @tparam T The type of the value held by the feature.
 * @tparam F The type of the feature itself, which must extend [[Feature]].
 */
trait Feature[T, F <: Feature[T, F]] extends Verifiable with FlatMappable[T] with Foldable[T]:
    /** The value held by the feature. */
    def value: T

    /** Creates a duplicate of the feature with the specified value.
     *
     * @param value The value for the new feature.
     * @return A new feature with the specified value.
     */
    def duplicateWithValue(value: T): F
