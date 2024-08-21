package cl.ravenhill.plascevo
package composerr.constraints

import composerr.constraints.Constraint

/**
 * A trait representing a constraint that checks whether a value is negative based on a given zero value.
 *
 * The `BeNegativeConstraint` trait extends the `Constraint` trait and is used to validate that a given value of type
 * `T` is less than a specified `zero` value. The comparison is performed using an implicit `Ordering[T]`, making the
 * trait applicable to any type `T` that has an implicit ordering defined. This trait is useful for ensuring that values
 * are negative in the context of custom ordering logic.
 *
 * @tparam T The type of value that this constraint applies to.
 * @param zero The value of type `T` that represents zero, against which other values will be compared to determine if
 *             they are negative.
 * @param ord  An implicit `Ordering[T]` that defines how values of type `T` are compared.
 * @example
 * {{{
 * // Example of using `BeNegativeConstraint` with an integer:
 * object BeNegativeIntConstraint extends BeNegativeConstraint(0)
 *
 * val value = -5
 * if !BeNegativeIntConstraint.validator(value) then
 *   throw BeNegativeIntConstraint.generateException(s"Value $value is not negative.")
 *
 * // Example of using `BeNegativeConstraint` with a custom type:
 * case class CustomNumber(n: Int)
 *
 * object CustomNumber:
 *   given Ordering[CustomNumber] = Ordering.by(_.n)
 *
 * object BeNegativeCustomNumberConstraint extends BeNegativeConstraint(CustomNumber(0))
 *
 * val customValue = CustomNumber(-10)
 * if !BeNegativeCustomNumberConstraint.validator(customValue) then
 *   throw BeNegativeCustomNumberConstraint.generateException(s"Value $customValue is not negative.")
 * }}}
 */
trait BeNegativeConstraint[T](val zero: T)(using ord: Ordering[T]) extends Constraint[T]:

    /**
     * Validates whether the given value is negative compared to the specified `zero` value.
     *
     * This method uses the provided `Ordering[T]` to check if the input value is less than the `zero` value, returning
     * `true` if the value is considered negative, and `false` otherwise.
     */
    override val validator: T => Boolean = ord.lt(_, zero)
