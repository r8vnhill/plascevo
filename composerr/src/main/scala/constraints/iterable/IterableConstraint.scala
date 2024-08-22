package cl.ravenhill.composerr
package constraints.iterable

import constraints.Constraint
import exceptions.{ConstraintException, IterableConstraintException}

/**
 * A trait representing a constraint on an iterable collection of elements of type `T`.
 *
 * The `IterableConstraint` trait extends the `Constraint` trait to apply validation logic to collections of elements.
 * It overrides the `generateException` method to produce an `IterableConstraintException` when a constraint on the
 * collection is violated.
 *
 * @tparam T The type of elements contained within the iterable collection that this constraint applies to.
 * @example
 * {{{
 * // Example of an IterableConstraint for non-empty collections:
 * object NonEmptyCollectionConstraint extends IterableConstraint[Int] {
 *   val validator: Iterable[Int] => Boolean = _.nonEmpty
 * }
 *
 * val numbers = List.empty[Int]
 * if (!NonEmptyCollectionConstraint.validator(numbers)) {
 *   throw NonEmptyCollectionConstraint.generateException("The collection must not be empty.")
 * }
 * }}}
 */
trait IterableConstraint[T] extends Constraint[Iterable[T]] {

    /**
     * Generates an `IterableConstraintException` with a given description.
     *
     * This method is used to create an exception specifically for violations of constraints on iterable collections.
     *
     * @param description A message that explains the constraint violation for the iterable collection.
     * @return An `IterableConstraintException` with the provided description.
     */
    override def generateException(description: String): IterableConstraintException =
        IterableConstraintException(description)
}
