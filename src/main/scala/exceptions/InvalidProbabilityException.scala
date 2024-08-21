package cl.ravenhill.plascevo
package exceptions

import cl.ravenhill.composerr.exceptions.ConstraintException

/**
 * An exception that is raised when a probability value is invalid.
 *
 * The `InvalidProbabilityException` class extends the `ConstraintException` class and is specifically used for
 * scenarios where a probability value is outside the valid range (typically between 0 and 1, inclusive).
 * This class provides a mechanism to signal errors related to invalid probability values in a clear and consistent
 * manner.
 *
 * @param message A message that describes the constraint violation related to the probability value.
 *
 * @example
 * {{{
 * // Example of throwing an `InvalidProbabilityException`:
 * val probability = 1.5
 * constrained {
 *     "Probability must be between 0.0 and 1.0" ~ (
 *         probability must BeInRange(0.0, 1.0),
 *         InvalidProbabilityException(_)
 *     )
 * }
 * // Throws a `CompositeException` with a single `InvalidProbabilityException` containing the message
 * // "Probability must be between 0.0 and 1.0".
 * }}}
 */
class InvalidProbabilityException(message: String) extends ConstraintException(message)
