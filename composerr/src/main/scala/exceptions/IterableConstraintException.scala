package cl.ravenhill.composerr
package exceptions

/**
 * An exception that is raised when a constraint on an iterable collection is violated.
 *
 * The `IterableConstraintException` class extends the `ConstraintException` and is used specifically for scenarios
 * where constraints applied to an iterable collection (e.g., lists, sets, etc.) are not satisfied.
 *
 * @constructor Creates a new `IterableConstraintException` with a specified error message.
 *
 * @param message The detail message that describes the constraint violation for the iterable collection.
 *
 * @example
 * {{{
 * // Example of throwing an `IterableConstraintException`:
 * val exception = new IterableConstraintException("The collection contains invalid elements.")
 * throw exception
 * }}}
 */
class IterableConstraintException(message: String) extends ConstraintException(message)
