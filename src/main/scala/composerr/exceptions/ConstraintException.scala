package cl.ravenhill.composerr
package exceptions

/**
 * A base class for exceptions that are raised due to constraint violations.
 *
 * The `ConstraintException` class serves as a foundation for exceptions related to constraints within a system. It can
 * be extended by other exception classes to represent specific types of constraint violations.
 *
 * @constructor Creates a new `ConstraintException` with a specified error message.
 *
 * @param message The detail message that describes the constraint violation.
 *
 * @example
 * {{{
 * // Example of throwing a custom constraint exception:
 * class MaxLengthExceededException extends ConstraintException("Maximum length exceeded")
 *
 * throw new MaxLengthExceededException()
 * }}}
 */
open class ConstraintException(message: String) extends Exception(message)
