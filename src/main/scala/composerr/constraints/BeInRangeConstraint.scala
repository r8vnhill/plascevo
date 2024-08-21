package cl.ravenhill.plascevo
package composerr.constraints

/**
 * A constraint that validates whether a value is within a specified range.
 *
 * The `BeInRangeConstraint` trait extends the `Constraint[T]` trait and is used to check if a value of type `T` lies
 * within a specified inclusive range, defined by the `min` and `max` values. The comparison is performed using an
 * implicit `Ordering[T]`, making this trait applicable to any type `T` that has an ordering defined. This trait is
 * meant to be used as a mixin trait for specific range constraints, such as `ints.BeInRange` or `doubles.BeInRange`.
 *
 * @tparam T The type of value that this constraint applies to.
 *
 * @param min The minimum allowable value of type `T`.
 * @param max The maximum allowable value of type `T`.
 * @param ord An implicit `Ordering[T]` that defines how values of type `T` are compared.
 *
 * @example
 * {{{
 * // Example with integers
 * val beInRange = BeInRangeConstraint(1, 10) {}
 * constrained {
 *     "Value must be between 1 and 10" | { 5 must beInRange }
 *     "Value must be between 1 and 10" ~ (15 mustNot beInRange, CustomConstraintException(_))
 * }
 *
 * // Example with a custom type
 * case class CustomNumber(n: Int)
 *
 * given Ordering[CustomNumber] = Ordering.by(_.n)
 * val beInRangeCustom = BeInRangeConstraint(CustomNumber(1), CustomNumber(10)) {}
 * constrained {
 *     "CustomNumber must be between 1 and 10" | { CustomNumber(7) must beInRangeCustom }
 *     "CustomNumber must be between 1 and 10" ~ (
 *         CustomNumber(15) mustNot beInRangeCustom,
 *         CustomConstraintException(_)
 *     )
 * }
 *
 * // Example of using `BeInRangeConstraint` as a mixin
 * class BeInRangeTen extends BeInRangeConstraint[Int](1, 10) with IntConstraint
 *
 * val beInRangeTen = BeInRangeTen()
 * constrained {
 *     "Value must be between 1 and 10" in { 7 must beInRangeTen }
 *     "Value must be between 1 and 10" in { 11 mustNot beInRangeTen }
 * }
 * }}}
 */
trait BeInRangeConstraint[T](val min: T, val max: T)(using ord: Ordering[T]) extends Constraint[T] {

    /**
     * Validates whether the given value is within the specified range.
     *
     * This method checks if the input value is greater than or equal to the `min` value and less than or equal to the
     * `max` value, returning `true` if the value satisfies these conditions, and `false` otherwise.
     */
    override val validator: T => Boolean = { value =>
        ord.gteq(value, min) && ord.lteq(value, max)
    }
}
