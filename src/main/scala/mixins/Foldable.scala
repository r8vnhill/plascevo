package cl.ravenhill.plascevo
package mixins

/** A trait that defines a foldable structure, allowing for aggregation of elements.
 *
 * The `Foldable` trait provides two methods, `foldLeft` and `foldRight`, which can be used to combine elements of the
 * structure into a single value by iterating over the elements and applying a binary operation.
 *
 * @tparam T The type of elements contained within the foldable structure.
 */
trait Foldable[T] {

    /** Folds the elements of the structure from the left.
     *
     * This method starts with an initial value and applies the provided binary operation sequentially from the left
     * (beginning) of the structure to the right (end), combining the elements into a single value.
     *
     * @param initial The initial value to start the folding operation.
     * @param f       A binary operation that takes the current accumulated value and the next element, and returns the
     *                new accumulated value.
     * @tparam U      The type of the accumulated value and the result.
     * @return The result of folding the structure from the left.
     */
    def foldLeft[U](initial: U)(f: (U, T) => U): U

    /** Folds the elements of the structure from the right.
     *
     * This method starts with an initial value and applies the provided binary operation sequentially from the
     * right (end) of the structure to the left (beginning), combining the elements into a single value.
     *
     * @param initial The initial value to start the folding operation.
     * @param f       A binary operation that takes the next element and the current accumulated value, and returns the
     *                new accumulated value.
     * @tparam U      The type of the accumulated value and the result.
     * @return The result of folding the structure from the right.
     */
    def foldRight[U](initial: U)(f: (T, U) => U): U
}
