package cl.ravenhill.composerr
package exceptions


/**
 * An exception that is raised when a constraint on a `Double` value is violated.
 *
 * The `DoubleConstraintException` class extends the `ConstraintException` class and is specifically used for scenarios
 * where constraints applied to `Double` values are not satisfied. This class provides a mechanism to signal errors
 * related to floating-point constraints in a clear and consistent manner.
 *
 * @param message A message that describes the constraint violation related to the `Double` value.
 *
 * @example
 * {{{
 * constrained {
 *     "Value must be less than 1.0" | (value must BeInRange(Double.MinValue, 1.0))
 *     // Throws a CompositeException with a DoubleConstraintException if the value is greater than 1.0.
 * }
 * }}}
 */
class DoubleConstraintException(message: String) extends ConstraintException(message)
