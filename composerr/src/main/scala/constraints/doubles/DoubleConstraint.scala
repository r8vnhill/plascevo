package cl.ravenhill.composerr
package constraints.doubles

import constraints.Constraint
import exceptions.{ConstraintException, DoubleConstraintException}

/**
 * A trait for defining constraints on `Double` values.
 *
 * The `DoubleConstraint` trait extends the `Constraint[Double]` trait and provides a foundation for creating
 * constraints specific to `Double` values. It overrides the `generateException` method to produce a
 * `DoubleConstraintException` when a constraint on a `Double` value is violated. This trait is designed to be mixed
 * into specific constraint classes that operate on `Double` values.
 *
 * @example
 * {{{
 * // Example of using `DoubleConstraint` in a custom constraint class
 * class BeAtMostOne extends DoubleConstraint {
 *     override val validator: Double => Boolean = _ <= 1.0
 * }
 *
 * val beAtMostOne = BeAtMostOne()
 * constrained {
 *     "Value must be at most 1.0" | (value must beAtMostOne)
 *     // If the value is greater than 1.0, a CompositeException is thrown with a DoubleConstraintException
 * }
 * }}}
 */
trait DoubleConstraint extends Constraint[Double] {

    /**
     * Generates a `DoubleConstraintException` with a given description.
     *
     * This method is used to create an exception specifically for violations of constraints on `Double` values.
     *
     * @param description A message that explains the constraint violation related to the `Double` value.
     * @return A `DoubleConstraintException` with the provided description.
     */
    override def generateException(
        description: String
    ): DoubleConstraintException = DoubleConstraintException(description)
}
