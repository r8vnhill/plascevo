package cl.ravenhill.plascevo
package mixins

/** A trait representing an object with a defined range of values.
 *
 * The `Ranged` trait provides a mechanism for defining boundaries or limits within which an object's value must lie.
 * This is particularly useful in scenarios where constraints are necessary, such as in evolutionary algorithms, 
 * where genes may need to adhere to specific value ranges.
 *
 * The `range` method returns a tuple representing a closed interval from a start to an end point, both inclusive. 
 * Implementing classes must provide this range, ensuring that the object's value is within the specified bounds.
 *
 * @tparam T The type of value that the range constrains. This type must have an implicit ordering defined.
 *
 * @example
 * {{{
 * // Example implementation of a Ranged trait for integers
 * case class IntRangedValue(value: Int, min: Int, max: Int) extends Ranged[Int] {
 *   override def range(implicit ord: Ordering[Int]): (Int, Int) = (min, max)
 * }
 *
 * val rangedValue = IntRangedValue(5, 0, 10)
 * val range = rangedValue.range
 * // range: (Int, Int) = (0, 10)
 * }}}
 */
trait Ranged[T] {

    /** The range defining the boundaries or limits of the object.
     *
     * This property represents a closed interval from a start to an end point, both included. It is essential for
     * scenarios where the object's value must be constrained within certain bounds.
     *
     * @param ord An implicit ordering for the type `T`, which ensures that the range is correctly defined and
     *            comparable.
     * @return A tuple representing the range, where the first element is the start of the range, and the second element
     *         is the end of the range.
     */
    def range(implicit ord: Ordering[T]): (T, T)
}
