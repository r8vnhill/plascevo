package cl.ravenhill.composerr
package constraints.ints

import constraints.BePositiveConstraint

/**
 * A constraint that validates whether an integer value is positive.
 *
 * The `BePositive` object extends the `BePositiveConstraint[Int]` and `IntConstraint` traits to provide a specific 
 * constraint for validating that an integer value is greater than zero. This object is designed to be used in contexts 
 * where you need to ensure that a value of type `Int` is strictly positive. If the value does not meet this condition, 
 * an `IntConstraintException` can be generated.
 *
 * @example
 * {{{
 * val value = 5
 * constrained {
 *     "Value must be positive" ~ (
 *         value must BePositive,
 *         IntConstraintException(_)
 *     )
 * }
 * // This will pass because `value` is greater than zero.
 *
 * val negativeValue = -3
 * constrained {
 *     "Value must be positive" | (
 *         negativeValue must BePositive,
 *         IntConstraintException(_)
 *     )
 * }
 * // This will throw a `CompositeException` containing an `IntConstraintException` with the message
 * // "Value must be positive" because `negativeValue` is not greater than zero.
 * }}}
 */
object BePositive extends BePositiveConstraint[Int](0) with IntConstraint
