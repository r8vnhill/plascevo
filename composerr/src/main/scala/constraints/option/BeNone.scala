package cl.ravenhill.composerr
package constraints.option

/**
 * A constraint that validates whether an `Option` value is `None`.
 *
 * The `BeNone` object extends the `OptionConstraint` trait and provides a specific constraint for checking if an
 * `Option` value is `None`. This constraint ensures that the `Option` contains no value, and if the validation fails,
 * an `OptionConstraintException` can be generated.
 *
 * @example
 * {{{
 * val optionValue: Option[Int] = None
 * constrained {
 *     "Option value must be None" ~ (
 *         optionValue mustNot BeNone,
 *         OptionConstraintException(_)
 *     )
 * }
 * // Throws a `CompositeException` with a single `OptionConstraintException` if the `Option` value is not `None`.
 *
 * val someValue: Option[Int] = Some(42)
 * constrained {
 *     "Option value must be None" ~ (
 *         someValue must BeNone,
 *         OptionConstraintException(_)
 *     )
 * }
 * // Throws a `CompositeException` with a single `OptionConstraintException` containing the message
 * // "Option value must be None".
 * }}}
 */
object BeNone extends OptionConstraint {

    override val validator: Option[?] => Boolean = {
        case None => true
        case _ => false
    }
}
