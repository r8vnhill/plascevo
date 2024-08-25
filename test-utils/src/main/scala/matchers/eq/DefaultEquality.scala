/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package matchers.eq

import assertions.print.PrintedWithType
import assertions.{ActualWithType, ExpectedWithType}
import matchers.ApplyMatcher.failureWithTypeInformation

object DefaultEquality extends Eq[Any] {

    /** Compares two values for equality and returns an optional error if they are not equal.
     *
     * The `equals` method checks whether the `actual` and `expected` values are equal according to the provided
     * equality rules. The comparison can be performed with either strict or non-strict equality, depending on the
     * `strictNumberEq` parameter. If the values are not equal, the method returns an `Option[Throwable]` containing
     * detailed information about the failure, including the expected and actual values along with their types.
     *
     * @param actual         The actual value being compared.
     * @param expected       The expected value to compare against.
     * @param strictNumberEq The `NumberEquality` mode to use for comparing numerical values. It determines whether the
     *                       comparison should be strict (e.g., exact type matches) or non-strict (e.g., allowing type
     *                       conversions).
     * @return An `Option[Throwable]` containing a failure description if the values are not equal, or `None` if they are
     *         equal.
     */
    override def equals(
        actual: Any,
        expected: Any
    )(using strictNumberEq: EqualityMatcher.NumberEquality): Option[Throwable] = if (test(actual, expected)) {
        None
    } else {
        Some(
            failureWithTypeInformation(
                ExpectedWithType(PrintedWithType(Some(expected), expected.getClass.getTypeName)),
                ActualWithType(PrintedWithType(Some(actual), actual.getClass.getTypeName))
            )
        )
    }

    /** Compares two values for equality, with special handling for arrays.
     *
     * The `test` function checks whether two provided values, `actual` and `expected`, are equal. It accounts for the 
     * possibility that the values may be arrays, in which case it compares the contents of the arrays element by
     * element. The function also handles cases where either value may be `null`.
     *
     * @param actual   The first value to be compared.
     * @param expected The second value to be compared.
     * @return `true` if the values are considered equal, `false` otherwise.
     */
    private def test(actual: Any, expected: Any): Boolean = (actual, expected) match {
        case (null, null) => true
        case (null, _) | (_, null) => false
        case (a: Array[_], b: Array[_]) => a.sameElements(b)
        case (a: Array[_], b: Any) => a.sameElements(Array(b))
        case (a: Any, b: Array[_]) => Array(a).sameElements(b)
        case (a: Any, b: Any) => a == b
    }
}
