/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.matchers
package matchers.eq

import matchers.ApplyMatcher.failureWithTypeInformation

import cl.ravenhill.munit.assertions.{ActualWithType, ExpectedWithType}
import cl.ravenhill.munit.collectors.ErrorCollector
import cl.ravenhill.munit.print.PrintedWithType
import munit.ComparisonFailException

object NumberEq extends Eq[Number] {

    /** Compares two `Number` instances for equality, taking into account strictness settings.
     *
     * The `equals` method checks if two `Number` instances, `actual` and `expected`, are equal. It performs the
     * comparison based on the specified `strictNumberEq` mode. If the numbers are considered equal under the given
     * mode, the method returns `None`, indicating that no error occurred. Otherwise, it returns an `Option` containing
     * a `Throwable` that describes the failure, including detailed type information for both the expected and actual
     * values.
     *
     * @param actual         The actual `Number` instance to be compared.
     * @param expected       The expected `Number` instance to be compared against.
     * @param strictNumberEq The mode of equality comparison, which can be either strict or non-strict, as defined by
     *                       `EqualityMatcher.NumberEquality`.
     * @return `None` if the numbers are equal according to the comparison mode; otherwise, an `Option[Throwable]`
     *         containing a failure description.
     */
    override def equals(
        actual: Number,
        expected: Number,
    )(
        using
        strictNumberEq: EqualityMatcher.NumberEquality,
        errorCollector: ErrorCollector
    ): Option[ComparisonFailException] =
        if compare(actual, expected, strictNumberEq) then
            None
        else
            Some(
                failureWithTypeInformation(
                    ExpectedWithType(PrintedWithType(Some(expected), expected.getClass.getTypeName)),
                    ActualWithType(PrintedWithType(Some(actual), actual.getClass.getTypeName))
                )
            )

    /** Compares two numeric values with optional strict equality.
     *
     * The `compare` method evaluates the equality between two `Number` values, `a` and `b`. Depending on the
     * `strictNumberEq` parameter, the comparison can either be strict or non-strict.
     *
     * In strict mode, the method checks for exact equality between `a` and `b`, meaning they must be of the same type
     * and have the same value. In non-strict mode, the method allows for comparisons across different numeric types by
     * converting the values appropriately before comparing them.
     *
     * @param a              The first `Number` to compare.
     * @param b              The second `Number` to compare.
     * @param strictNumberEq A parameter indicating whether the comparison should be strict or non-strict.
     * @return `true` if the numbers are considered equal based on the specified mode, `false` otherwise.
     */
    private def compare(
        a: Number,
        b: Number,
        strictNumberEq: EqualityMatcher.NumberEquality
    ): Boolean = strictNumberEq match {
        case EqualityMatcher.NumberEquality.Strict => a == b
        case EqualityMatcher.NumberEquality.NonStrict =>
            (a, b) match {
                case (a: Int, b: Long) => a.toLong == b
                case (a: Int, b: Double) => a.toDouble == b
                case (a: Int, b: Float) => a.toFloat == b
                case (a: Int, b: Short) => a == b.toInt
                case (a: Int, b: Byte) => a == b.toInt
                case (a: Float, b: Double) => a.toDouble == b
                case (a: Float, b: Int) => a == b.toFloat
                case (a: Double, b: Float) => a == b.toDouble
                case (a: Double, b: Int) => a == b.toDouble
                case (a: Double, b: Short) => a == b.toDouble
                case (a: Double, b: Byte) => a == b.toDouble
                case (a: Long, b: Int) => a == b.toLong
                case (a: Long, b: Short) => a == b.toLong
                case (a: Long, b: Byte) => a == b.toLong
                case (a: Short, b: Long) => a.toLong == b
                case (a: Short, b: Int) => a.toInt == b
                case (a: Short, b: Byte) => a == b.toShort
                case _ => a == b
            }

    }
}
