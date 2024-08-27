package cl.ravenhill.plascevo
package exceptions

import cl.ravenhill.composerr.exceptions.ConstraintException

/** An exception that indicates an invalid size constraint in an evolutionary algorithm.
 *
 * The `InvalidSizeException` class extends the `ConstraintException` and is used to signal an error when a size
 * constraint is violated. This exception is typically thrown when the size of a collection, such as a population or
 * a subset, does not meet the required constraints defined by the algorithm.
 *
 * @param message A descriptive message explaining the reason for the exception.
 * @example
 * {{{
 * constrained {
 *    "Cannot create a sequence with a negative size." ~ (size mustNot BeNegative, InvalidSizeException(_))
 * }
 * }}}
 */
class InvalidSizeException(message: String) extends ConstraintException(message)
