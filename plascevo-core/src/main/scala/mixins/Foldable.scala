package cl.ravenhill.plascevo
package mixins


/** Represents a structure that can be folded from the left or right.
 *
 * The `Foldable` trait defines an interface for structures that can be sequentially reduced or transformed into a
 * single value through a folding operation. Folding is a common operation in functional programming, where a
 * collection of elements is combined into a single result by repeatedly applying a binary operation.
 *
 * This trait provides two methods: `foldLeft`, which processes elements from left to right, and `foldRight`, which
 * processes elements from right to left. These methods are useful in scenarios where the order of operations matters,
 * such as when summing values, constructing data structures, or evaluating expressions.
 *
 * The choice between `foldLeft` and `foldRight` can also impact performance. For example, folding large lists to build
 * another list is generally more efficient with `foldRight`, as it avoids repeatedly appending to the end of the list.
 * On the other hand, folding with operations like summation or string concatenation might be more efficient with
 * `foldLeft` because it avoids stack overflows for large collections.
 *
 * @tparam T The type of elements contained within the structure.
 *
 * @example
 * {{{
 * // Example implementation for a simple list wrapper
 * class IntList(val elements: List[Int]) extends Foldable[Int] {
 *   override def foldLeft[U](initial: U)(f: (U, Int) => U): U = elements.foldLeft(initial)(f)
 *
 *   override def foldRight[U](initial: U)(f: (Int, U) => U): U = elements.foldRight(initial)(f)
 * }
 *
 * val list = new IntList(List(1, 2, 3, 4))
 *
 * // Folding from the left to concatenate strings (efficient):
 * val concatenatedLeft = list.foldLeft("")((acc, n) => acc + n.toString)
 * // concatenatedLeft: String = "1234"
 *
 * // Folding from the right to construct a reversed list (efficient):
 * val reversedRight = list.foldRight(List.empty[Int])((n, acc) => acc :+ n)
 * // reversedRight: List[Int] = List(4, 3, 2, 1)
 * }}}
 */
trait Foldable[T] {

    /** Folds the elements of the structure from the left, combining them with an initial value.
     *
     * The `foldLeft` method processes the elements of the structure from left to right, starting with the provided
     * `initial` value. It applies the binary operation `f` to combine the current accumulated result with each element
     * in turn. This method is useful for operations where the order of processing matters, such as calculating sums,
     * constructing lists, or reducing collections.
     *
     * Folding from the left is often more efficient when building up results that naturally associate from left to
     * right, such as summing numbers or concatenating strings. However, it can be less efficient for operations that
     * involve appending elements to a sequence, as it may require repeatedly traversing the accumulated result.
     *
     * @param initial The initial value for the folding operation, serving as the starting point.
     * @param f       A binary function that takes the current accumulated value and the next element, returning the
     *                updated accumulated value.
     * @tparam U The type of the accumulated value and the final result.
     * @return The final result of folding the elements from the left using the provided function `f`.
     */
    def foldLeft[U](initial: U)(f: (U, T) => U): U

    /** Folds the elements of the structure from the right, combining them with an initial value.
     *
     * The `foldRight` method processes the elements of the structure from right to left, starting with the provided
     * `initial` value. It applies the binary operation `f` to combine each element with the current accumulated result.
     * This method is useful for operations where the right-to-left order of processing matters, such as constructing
     * lists or evaluating expressions in reverse order.
     *
     * Folding from the right can be more efficient than `foldLeft` when constructing sequences like lists, as it
     * naturally builds the result from the rightmost element and avoids repeatedly traversing the accumulated result.
     * However, it can be less efficient for operations that naturally associate from left to right, like summing
     * numbers, especially in very large collections where `foldRight` might cause stack overflow issues.
     *
     * @param initial The initial value for the folding operation, serving as the starting point.
     * @param f       A binary function that takes the next element and the current accumulated value, returning the
     *                updated accumulated value.
     * @tparam U The type of the accumulated value and the final result.
     * @return The final result of folding the elements from the right using the provided function `f`.
     */
    def foldRight[U](initial: U)(f: (T, U) => U): U
}
