package cl.ravenhill.plascevo
package composerr.constraints.doubles

import composerr.constraints.BeInRangeConstraint

/**
 * A constraint that validates whether a `Double` value is within a specified range.
 *
 * The `BeInRange` class extends both the `BeInRangeConstraint[Double]` and `DoubleConstraint` traits to provide a 
 * specific constraint for validating that a `Double` value lies within a specified inclusive range. The range is 
 * defined by the `min` and `max` values. If the value does not fall within this range, a `DoubleConstraintException`
 * is generated.
 *
 * @param min The minimum allowable value of type `Double`.
 * @param max The maximum allowable value of type `Double`.
 *
 * @example
 * {{{
 * // Example of using `BeInRange` to validate a `Double` value
 * val beInRange = BeInRange(0.0, 1.0)
 * constrained {
 *     "Value must be between 0.0 and 1.0" | (value must beInRange)
 *     // If the value is outside the range [0.0, 1.0], a CompositeException is thrown with a DoubleConstraintException
 * }
 * }}}
 */
final class BeInRange(min: Double, max: Double) extends BeInRangeConstraint[Double](min, max) with DoubleConstraint
