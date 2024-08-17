package cl.ravenhill.plascevo
package mixins

/** A trait representing a structure that can be flattened and flat-mapped.
 *
 * The `FlatMappable` trait provides an interface for types that can be flattened into a `List` and then further
 * transformed using a `flatMap` operation.
 *
 * <h2>Usage:</h2>
 * Implement this trait for any custom data structure that you want to support flattening and flat-mapping operations.
 *
 * <h3>Example:</h3>
 * @example
 * {{{
 * class Container[+T](elements: List[T]) extends FlatMappable[T] {
 *   def flatten(): List[T] = elements
 * }
 * }}}
 *
 * @tparam T The type of the elements contained in the structure.
 */
trait FlatMappable[+T]:

    /** Flattens the structure into a `List` of elements.
     *
     * @return A `List` containing all the elements in the structure.
     */
    def flatten(): Seq[T]

    /** Applies a function to each element in the structure and flattens the resulting lists.
     *
     * @param f A function that transforms each element into a `List` of elements.
     * @tparam U The type of elements in the resulting list.
     * @return A `List` containing all the elements produced by applying `f` to each element and flattening the results.
     */
    def flatMap[U](f: T => Seq[U]): Seq[U] = flatten().flatMap(f)
