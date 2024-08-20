package cl.ravenhill.plascevo
package mixins

/** Represents a structure that can be flattened and mapped over in an evolutionary algorithm or other contexts.
 *
 * The `FlatMappable` trait provides an abstraction for structures that can be "flattened" into a sequence of elements
 * and subsequently "flat-mapped" over, applying a transformation function to each element. This is particularly useful
 * in scenarios where you need to reduce nested structures into a single sequence or perform operations across all
 * elements within the structure.
 *
 * This trait is designed to be flexible and general, allowing for various implementations where a structure can be
 * decomposed into a sequence of its constituent elements. For example, in a genetic algorithm, a genotype might
 * implement this trait to allow easy manipulation of its genes.
 *
 * @tparam T The type of elements contained within the structure.
 *
 * @example
 * {{{
 * // Example implementation for a simple wrapper around a sequence of integers
 * class IntegerWrapper(val numbers: Seq[Int]) extends FlatMappable[Int] {
 *   override def flatten(): Seq[Int] = numbers
 * }
 *
 * val wrapper = new IntegerWrapper(Seq(1, 2, 3))
 * val flatMapped = wrapper.flatMap(n => Seq(n, n * 2))
 * // flatMapped: Seq[Int] = Seq(1, 2, 2, 4, 3, 6)
 * }}}
 */
trait FlatMappable[+T] {

    /** Flattens the structure into a sequence of elements.
     *
     * The `flatten` method reduces the structure into a single sequence of its constituent elements. This is useful
     * for converting nested or complex structures into a simpler form that can be further processed or analyzed.
     *
     * @return A sequence containing all the elements within the structure.
     */
    def flatten(): Seq[T]

    /** Applies a function to each element in the flattened structure and concatenates the results.
     *
     * The `flatMap` method first flattens the structure into a sequence and then applies the provided function `f` to
     * each element. The resulting sequences from each function application are concatenated into a single sequence.
     *
     * @param f A function that transforms each element of type `T` into a sequence of elements of type `U`.
     * @tparam U The type of elements in the resulting sequence.
     * @return A sequence of elements obtained by applying the function `f` to each element in the flattened structure.
     */
    def flatMap[U](f: T => Seq[U]): Seq[U] = flatten().flatMap(f)
}
