package cl.ravenhill.plascevo
package mixins

/** A trait that represents a structure that can be mapped over.
 *
 * The `Mappable` trait provides an interface for applying a transformation function to the elements of a structure,
 * producing a sequence of transformed elements. It is designed to be extended by classes or traits that contain
 * elements which can be mapped to another type.
 *
 * @tparam T The type of elements contained within the structure.
 */
trait Mappable[T]:

    /** Applies a transformation function to each element in the structure.
     *
     * This method takes a function `f` that transforms elements of type `T` to type `U` and applies it to each element
     * in the structure, returning a sequence of transformed elements.
     *
     * @param f The transformation function to apply to each element.
     * @tparam U The type of elements in the resulting sequence.
     * @return A sequence of elements of type `U` resulting from applying the transformation function to each element in
     *         the structure.
     */
    def map[U](f: T => U): Seq[U]
