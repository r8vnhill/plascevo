package constraints.ints

import cl.ravenhill.plascevo.composerr.constraints.BeNegativeConstraint
import cl.ravenhill.plascevo.composerr.constraints.ints.IntConstraint


/**
 * A constraint that checks whether an integer value is negative.
 *
 * The `BeNegative` object extends both `BeNegativeConstraint[Int]` and `IntConstraint` to provide a specific constraint
 * for validating that an integer is negative. It uses `0` as the reference value, meaning any integer less than `0`
 * will satisfy the constraint. If the integer is not negative, an `IntConstraintException` will be generated.
 *
 * @example
 * {{{
 * constrained {
 *     "The number must be negative" in {
 *         val number = -5
 *         number must BeNegative
 *     }
 * }
 * }}}
 */
object BeNegative extends BeNegativeConstraint[Int](0) with IntConstraint
