package cl.ravenhill.plascevo
package composerr.constraints.ints

import composerr.constraints.{BeAtMostConstraint, Constraint}

/**
 * A constraint that validates whether an integer value is at most a specified maximum.
 *
 * The `BeAtMost` class extends both the `Constraint[Int]` and `BeAtMostConstraint[Int]` traits to provide a specific
 * constraint for validating that an integer is less than or equal to a specified maximum value. This class uses an
 * integer as the maximum allowable value and leverages the `Ordering[Int]` to perform the comparison.
 *
 * @param max The maximum allowable integer value.
 *
 * @example
 * {{{
 * val beAtMostFive = BeAtMost(5)
 * constrained {
 *     "Value must be at most 5" in { 3 must beAtMostFive }
 *     "Value must be at most 5" in { 7 mustNot beAtMostFive }
 * }
 * }}}
 */
class BeAtMost(max: Int) extends Constraint[Int] with BeAtMostConstraint[Int](max)
