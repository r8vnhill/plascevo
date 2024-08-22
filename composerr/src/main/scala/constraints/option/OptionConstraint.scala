package cl.ravenhill.composerr
package constraints.option

import constraints.Constraint

/**
 * A trait for defining constraints on `Option` values.
 *
 * The `OptionConstraint` trait extends the `Constraint[Option[?]]` trait and provides a foundation for creating
 * constraints specific to `Option` values. It overrides the `generateException` method to produce an
 * `OptionConstraintException` when a constraint on an `Option` value is violated. This trait is designed to be mixed
 * into specific constraint classes that operate on `Option` values, allowing developers to enforce rules such as
 * ensuring that an `Option` is defined or meets other criteria.
 *
 * @example
 * {{{
 * // Example of using `OptionConstraint` in a custom constraint class
 * object BeDefined extends OptionConstraint {
 *     override val validator: Option[?] => Boolean = _.isDefined
 * }
 *
 * val beDefined = BeDefined
 * val optionValue: Option[Int] = None
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
trait OptionConstraint extends Constraint[Option[?]]
