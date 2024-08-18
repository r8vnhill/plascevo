package cl.ravenhill.plascevo
package repr

import mixins.{FlatMappable, Foldable, Verifiable}

/** A trait representing a structure that holds features of a certain type and supports various operations.
 *
 * The `Representation` trait combines features from several interfaces, allowing it to be verifiable, foldable, and
 * flat-mappable. It encapsulates a collection of features, providing a foundation for more complex structures in
 * evolutionary algorithms.
 *
 * @tparam T The type of value stored by the feature.
 * @tparam F The kind of feature stored in the representation, which must implement [[Feature]].
 */
trait Representation[T, F <: Feature[T, F]] extends Verifiable with FlatMappable[T] with Foldable[T] {

    /** The size of the representation.
     *
     * This value represents the number of features contained within the representation.
     */
    def size: Int

    /** Checks if the representation is empty.
     *
     * @return `true` if the representation is empty, `false` otherwise.
     */
    def isEmpty: Boolean = size == 0
}