package cl.ravenhill.plascevo
package mixins


/** Represents a structure that can be mapped over to transform its elements.
 *
 * The `Mappable` trait defines an interface for structures that allow their elements to be transformed by applying
 * a function to each element. This transformation is achieved through the `map` method, which returns a new sequence
 * containing the results of applying the function `f` to each element in the original structure.
 *
 * This trait is commonly used in functional programming to apply a function over a collection or sequence of elements,
 * transforming them into a new sequence of results. The `map` operation is fundamental in many data processing tasks,
 * allowing for concise and expressive transformations.
 *
 * @tparam T The type of elements contained within the structure.
 *
 * @example
 * {{{
 * // Example implementation for a simple list wrapper
 * class IntList(val elements: List[Int]) extends Mappable[Int] {
 *   override def map[U](f: Int => U): Seq[U] = elements.map(f)
 * }
 *
 * val list = new IntList(List(1, 2, 3, 4))
 *
 * // Mapping over the list to square each element
 * val squaredList: Seq[Int] = list.map(x => x * x)
 * // squaredList: Seq[Int] = Seq(1, 4, 9, 16)
 * }}}
 */
trait Mappable[T] {

    /** Transforms the elements of the structure by applying a function to each element.
     *
     * The `map` method applies the function `f` to each element in the structure, producing a new sequence containing
     * the results. This method is particularly useful when you need to transform a collection of elements into another
     * collection with possibly different types or values.
     *
     * @param f A function that takes an element of type `T` and returns a value of type `U`.
     * @tparam U The type of elements in the resulting sequence.
     * @return A sequence of elements obtained by applying the function `f` to each element in the original structure.
     */
    def map[U](f: T => U): Seq[U]
}
