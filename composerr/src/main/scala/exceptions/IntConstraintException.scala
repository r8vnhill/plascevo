package cl.ravenhill.composerr
package exceptions

/**
 * An exception that is raised when an integer-related constraint is violated.
 *
 * The `IntConstraintException` class extends the `ConstraintException` class and is specifically used for scenarios
 * where constraints on integer values are not satisfied. It provides a custom string representation that clearly
 * indicates the nature of the exception.
 *
 * @constructor Creates a new `IntConstraintException` with a specified description.
 *
 * @param description A message that describes the constraint violation related to an integer value.
 *
 * @example
 * {{{
 * // Example of throwing an `IntConstraintException`:
 * val exception = new IntConstraintException("The value must be a positive integer.")
 * throw exception
 * }}}
 */
class IntConstraintException(description: String) extends ConstraintException(description) {

  /**
   * Returns a string representation of the `IntConstraintException`.
   *
   * The `toString` method provides a custom message format that includes the exception type and the description of the
   * violation.
   *
   * @return A string representation of this exception in the format: `IntConstraintException: <description>`.
   */
  override def toString: String = s"IntConstraintException: $description"
}
