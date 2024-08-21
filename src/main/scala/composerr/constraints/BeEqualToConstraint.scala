package cl.ravenhill.plascevo
package composerr.constraints

import composerr.constraints.Constraint

/**
 * A trait representing a constraint that checks whether a value is equal to an expected value.
 *
 * The `BeEqualToConstraint` trait extends the `Constraint` trait and is used to validate that a given value is equal to
 * a specified expected value. This constraint can be applied to any type `T` that supports equality comparison. If the
 * value does not equal the expected value, the validation will fail.
 *
 * @tparam T The type of value that this constraint applies to.
 * @param expected The expected value that the input value will be compared against.
 * @example
 * {{{
 * // Example of using `BeEqualToConstraint` to check if an integer is equal to 42:
 * object BeEqualTo42 extends BeEqualToConstraint* val value = 42
 * if (!BeEqualTo42.validator(value)) {
 *   throw BeEqualTo42.generateException(s"Value $value is not equal to the expected value 42.")
 * }
 *
 * // Example of using `BeEqualToConstraint` with a string:
 * object BeEqualToHello extends BeEqualToConstraint[String]("Hello")
 *
 * val greeting = "Hi"
 * if (!BeEqualToHello.validator(greeting)) {
 *   throw BeEqualToHello.generateException(s"Value '$greeting' is not equal to the expected value 'Hello'.")
 * }
 * }}}
 */
trait BeEqualToConstraint[T](expected: T) extends Constraint[T] {

    /**
     * Validates whether the given value is equal to the expected value.
     *
     * This method checks if the input value matches the expected value. It returns `true` if the values are equal, and
     * `false` otherwise.
     *
     * @return `true` if the value is equal to the expected value, `false` otherwise.
     */
    override val validator: T => Boolean = _ == expected
}
