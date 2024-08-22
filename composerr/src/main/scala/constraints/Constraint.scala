package cl.ravenhill.composerr
package constraints

import exceptions.ConstraintException

/**
 * A trait representing a constraint on a value of type `T`.
 *
 * The `Constraint` trait defines a contract for validating values of type `T`. It provides a `validator` function that
 * should be implemented to check whether a value satisfies the constraint. Additionally, it offers a method to generate
 * a `ConstraintException` when a value violates the constraint.
 *
 * @tparam T The type of value that the constraint applies to.
 * @constructor A new `Constraint` instance is created with a specific validation logic.
 * @example
 * {{{
 * // Example of a simple length constraint for strings:
 * object MaxLengthConstraint extends Constraint[String] {
 *   val validator: String => Boolean = _.length <= 10
 * }
 *
 * val input = "This is too long"
 * if (!MaxLengthConstraint.validator(input)) {
 *   throw MaxLengthConstraint.generateException("Input exceeds maximum length")
 * }
 * }}}
 */
trait Constraint[T] {

  /**
   * A function that checks if a value of type `T` satisfies the constraint.
   *
   * This function should return `true` if the value meets the constraint's requirements, and `false` otherwise.
   */
  val validator: T => Boolean

  /**
   * Generates a `ConstraintException` with a given description.
   *
   * This method can be used to create an exception that describes why a value violated the constraint.
   *
   * @param description A message that explains the constraint violation.
   * @return A `ConstraintException` with the provided description.
   */
  def generateException(description: String): ConstraintException = ConstraintException(description)
}
