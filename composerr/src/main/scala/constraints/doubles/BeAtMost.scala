/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.composerr
package constraints.doubles

import constraints.BeAtMostConstraint

/**
 * A constraint that validates whether a `Double` value is at most a specified maximum.
 *
 * The `BeAtMost` case class extends both `DoubleConstraint` and `BeAtMostConstraint[Double]`, providing a specific 
 * constraint to check if a `Double` value is less than or equal to the given maximum (`maxInclusive`). This constraint 
 * is useful in scenarios where you need to enforce an upper bound on a floating-point value.
 *
 * @param maxInclusive The maximum allowable value (inclusive) that the `Double` value can be.
 *
 * @example
 * {{{
 * val beAtMost100 = BeAtMost(100.0)
 *
 * val value1 = 99.9
 * constrained {
 *     "Value should be at most 100.0" | {
 *         value1 must beAtMost100
 *     }
 * }
 *
 * val value2 = 100.1
 * value2.constrainedTo {
 *     "Value should be at most 100.0" | {
 *         value2 must beAtMost100
 *     }
 * }
 * }}}
 */
final case class BeAtMost(maxInclusive: Double) extends DoubleConstraint with BeAtMostConstraint[Double](maxInclusive)
