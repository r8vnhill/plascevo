package cl.ravenhill.composerr
package exceptions

/**
 * An exception that is raised when a constraint on an `Option` value is violated.
 *
 * The `OptionConstraintException` class extends the `ConstraintException` class and is specifically used for scenarios
 * where constraints applied to `Option` values are not satisfied. This class provides a mechanism to signal errors
 * related to constraints on optional values in a clear and consistent manner.
 *
 * @param message A message that describes the constraint violation related to the `Option` value.
 * @example
 * {{{
 * // Example of throwing an `OptionConstraintException`:
 * val optionValue: Option[Int] = None
 * val beDefined: OptionConstraint = new OptionConstraint {
 *     override val validator: Option[Int] => Boolean = _.isDefined
 * }
 * constrained {
 *     "Option value must be defined" ~ (
 *         optionValue must beDefined,
 *         OptionConstraintException(_)
 *     )
 * }
 * // Throws a `CompositeException` with a single `OptionConstraintException` containing the message
 * // "Option value must be defined".
 * }}}
 */
class OptionConstraintException(message: String) extends ConstraintException(message)
