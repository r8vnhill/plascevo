package cl.ravenhill.composerr
package constraints.ints

import constraints.BeEqualToConstraint

/**
 * A constraint that checks whether an integer value is equal to an expected integer.
 *
 * The `BeEqualTo` class combines the functionality of `IntConstraint` and `BeEqualToConstraint[Int]` to validate that a
 * given integer value matches a specified expected integer. If the value does not equal the expected integer, the
 * validation will fail. This class is specifically designed for integer values and generates an
 * `IntConstraintException` in case of a violation.
 *
 * @param expected The expected integer value that the input value will be compared against.
 * @example
 * {{{
 * // Example of using `BeEqualTo` to check if an integer is equal to 42:
 * val constraint = new BeEqualTo(42)
 *
 * val value = 40
 * if (!constraint.validator(value)) {
 *   throw constraint.generateException(s"Value $value is not equal to the expected value 42.")
 * }
 * }}}
 */
class BeEqualTo(expected: Int) extends IntConstraint with BeEqualToConstraint[Int](expected)
