package cl.ravenhill.composerr
package constraints

import constraints.Constraint

/**
 * A constraint that validates whether a value is positive.
 *
 * The `BePositiveConstraint` trait extends the `Constraint[T]` trait and provides a mechanism for validating whether
 * a value of type `T` is greater than a specified `zero` value. The comparison is performed using an implicit
 * `Ordering[T]`, making this trait applicable to any type `T` that has an ordering defined. This trait is typically 
 * used to enforce that a value is strictly positive, meaning greater than zero.
 *
 * @param zero The value of type `T` that represents zero, used as the baseline for determining positivity.
 * @param ord An implicit `Ordering[T]` that defines how values of type `T` are compared.
 *
 * @tparam T The type of value that this constraint applies to.
 *
 * @example
 * {{{
 * // Example of using `BePositiveConstraint` with integers
 * object BePositiveInt extends BePositiveConstraint 
 *
 * val value = 5
 * constrained {
 *     "Value must be positive" ~ (
 *         value must BePositiveInt,
 *         CustomConstraintException(_)
 *     )
 * }
 * // Throws a `CompositeException` with a custom constraint exception if `value` is not positive.
 * }}}
 */
trait BePositiveConstraint[T](val zero: T)(using ord: Ordering[T]) extends Constraint[T]:

    /**
     * Validates whether the given value is positive.
     *
     * This method checks if the input value is greater than the specified `zero` value using the provided
     * `Ordering[T]`. It returns `true` if the value is strictly greater than `zero`, and `false` otherwise.
     *
     * @return `true` if the value is greater than `zero`, `false` otherwise.
     */
    override val validator: T => Boolean = ord.gt(_, zero)
