/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package matchers.eq

import cl.ravenhill.munit.print.Print.print
import matchers.ApplyMatcher
import matchers.eq.EqualityMatcher.NumberEquality.NonStrict
import matchers.eq.EqualityMatcher.{NumberEquality, equal}

import munit.Assertions

/** A type class for comparing values of type `T` for equality with optional strict number comparison.
 *
 * The `Eq` trait defines a contract for comparing two values of type `T` to determine if they are equal. The comparison
 * can be either strict or non-strict when dealing with numbers, depending on the provided `NumberEquality` parameter.
 *
 * @tparam T The type of values that this equality check is intended for.
 */
trait Eq[T] {

    /** Compares two values of type `T` for equality.
     *
     * This method compares the `actual` value to the `expected` value, with an optional parameter to determine if the
     * comparison should be strict or non-strict when dealing with numeric values.
     *
     * @param actual         The actual value to be compared.
     * @param expected       The expected value to compare against.
     * @param strictNumberEq An optional parameter indicating if the number comparison should be strict or non-strict.
     *                       Defaults to non-strict (`NonStrict`).
     * @return An `Option[Throwable]` that is `None` if the values are considered equal, or `Some(Throwable)` containing
     *         an error if they are not equal.
     */
    def equals(actual: T, expected: T)
        (using strictNumberEq: NumberEquality = NonStrict): Option[Throwable]
}

/** Object representing equality checks for values that may be `null`.
 *
 * The `NullEq` object provides an implementation of the `Eq` trait for comparing values that might be `null`. This
 * object handles cases where one or both of the values being compared are `null`, and returns an appropriate error
 * message if the comparison fails.
 *
 * This implementation is used to ensure that special cases involving `null` are handled gracefully, and that
 * meaningful error messages are generated when comparisons fail due to `null` values.
 */
object NullEq extends Eq[Any] {

    /** Compares two values for equality, handling `null` values.
     *
     * The `equals` method compares the `actual` and `expected` values, taking into account the possibility that one or
     * both values might be `null`. If both values are `null`, the method returns `None`, indicating that the comparison
     * was successful. If one value is `null` and the other is not, the method returns a corresponding error wrapped in
     * an `Option`. If neither value is `null`, but they are not equal, the method returns an
     * `IllegalArgumentException`.
     *
     * @param actual         The actual value to be compared, which may be `null`.
     * @param expected       The expected value to be compared, which may be `null`.
     * @param strictNumberEq A flag indicating whether strict number equality should be enforced. This parameter is
     *                       ignored in this implementation.
     * @return An `Option[Throwable]` containing an error if the comparison fails, or `None` if the values are equal.
     */
    override def equals(actual: Any, expected: Any)(using strictNumberEq: NumberEquality): Option[Throwable] = {
        (actual, expected) match {
            case (null, null) => None
            case (null, _) => Some(actualIsNull(expected))
            case (_, null) => Some(expectedIsNull(actual))
            case _ if actual != expected => Some(
                IllegalArgumentException("[$NullEq] should not be used when both values are not null")
            )
            case _ => None
        }
    }

    /** Creates an error when the actual value is `null` but the expected value is not.
     *
     * The `actualIsNull` method generates an `AssertionError` indicating that the actual value was `null` when a
     * non-null value was expected.
     *
     * @param expected The expected value, which was not `null`.
     * @return An `AssertionError` indicating that the actual value was `null`.
     */
    private def actualIsNull(expected: Any): AssertionError =
        ApplyMatcher.fail(s"Expected ${expected.print.value} but actual was null")

    /** Creates an error when the expected value is `null` but the actual value is not.
     *
     * The `expectedIsNull` method generates an `AssertionError` indicating that the expected value was `null` when a
     * non-null value was provided.
     *
     * @param actual The actual value, which was not `null`.
     * @return An `AssertionError` indicating that the expected value was `null`.
     */
    private def expectedIsNull(actual: Any): AssertionError =
        ApplyMatcher.fail(s"Expected null but actual was ${actual.print.value}")
}

object MapEq extends Eq[Map[?, ?]] {

    override def equals(actual: Map[?, ?], expected: Map[?, ?])
        (using strictNumberEq: NumberEquality): Option[Throwable] = {
        (actual, expected) match {
            case (null, null) => None
            case (_, null) => {
                val haveUnequalKeys = equal(actual.keys, expected.keys)
                if (haveUnequalKeys.isEmpty) {
                    generateError(actual, expected)
                    ???
                }
                ???
            }
        }
    }

    private def generateError(actual: Map[_, _], expected: Map[_, _]): Option[Throwable] = ???
}