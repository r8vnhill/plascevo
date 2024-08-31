/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.matchers
package matchers.eq

import matchers.{Matcher, MatcherResult, MatcherResultWithError}

import cl.ravenhill.munit.print.Printed

/** A matcher that checks for equality between an actual value and an expected value.
 *
 * The `EqualityMatcher` class extends the `Matcher` trait and is designed to compare an actual value with an expected
 * value. It evaluates whether the two values are equal, and returns a `MatcherResult` containing the result of the
 * comparison.
 *
 * @param expected The value that the actual value is expected to be equal to.
 * @tparam T The type of the values being compared.
 */
final class EqualityMatcher[T](expected: T) extends Matcher[T] {

    /** Compares the actual value with the expected value and returns the result.
     *
     * The `apply` method performs the equality check between the provided `value` and the `expected` value. It uses
     * the [[EqualityMatcher.equal]] method to determine if the two values are equal, and generates a 
     * [[MatcherResultWithError]] that includes information about whether the match succeeded or failed.
     *
     * @param value The actual value being compared against the expected value.
     * @return A [[MatcherResult]] containing the outcome of the equality check. If the values are not equal, the result
     *         includes a failure message. If they are equal, the result indicates success.
     */
    override def apply(value: T): MatcherResult = {
        val error = EqualityMatcher.equal(value, expected)

        MatcherResultWithError(
            cause = error,
            matches = error.isEmpty,
            failureMessageFn = _.map(_.getMessage)
                .getOrElse(
                    s"${Printed(Option(expected)).value.getOrElse("null")} should " +
                        s"be equal to ${Printed(Option(value)).value.getOrElse("null")}"
                ),
            negatedFailureMessageFn = _.map(_.getMessage)
                .getOrElse(
                    s"${Printed(Option(expected)).value.getOrElse("null")} should " +
                        s"not be equal to ${Printed(Option(value)).value.getOrElse("null")}"
                )
        )
    }
}


object EqualityMatcher {
    def equal[T](actual: T, expected: T)
        (using strictNumberEq: NumberEquality = NumberEquality.Strict): Option[Throwable] = {
        (actual, expected) match {
            // If the references are identical, there is no error
            case _ if actual.asInstanceOf[AnyRef] eq expected.asInstanceOf[AnyRef] => None
            // If either is null, handle null equality
            case (null, _) | (_, null) => NullEq.equals(actual, expected)
            // Handle specific cases based on the types of actual and expected
            case (a: Map[_, _], e: Map[_, _]) => MapEq.equals(a, e)
            //            case (a: Map.Entry[_, _], e: Map.Entry[_, _]) => MapEntryEq.equals(a, e, strictNumberEq)
            //            case (a: Regex, e: Regex) => RegexEq.equals(a, e)
            //            case (a: String, e: String) => StringEq.equals(a, e)
            case (a: Number, e: Number) => NumberEq.equals(a, e)
            //            case (a, e) if IterableEq.isValidIterable(a) && IterableEq.isValidIterable(e) =>
            //                IterableEq.equals(IterableEq.asIterable(a), IterableEq.asIterable(e), strictNumberEq)
            //            case (a: Seq[_], e: Seq[_]) => SequenceEq.equals(a, e, strictNumberEq)
            //            case _ if shouldShowDataClassDiff(actual, expected) =>
            //                DataClassEq.equals(actual.asInstanceOf[Any], expected.asInstanceOf[Any], strictNumberEq)
            //            case (a: Throwable, e: Throwable) => ThrowableEq.equals(a, e)
            //            // Default case for general equality
            case _ => DefaultEquality.equals(actual.asInstanceOf[Any], expected.asInstanceOf[Any])
        }
    }

    /** Enum representing the modes of number equality comparison.
     *
     * The `NumberEquality` enum defines two modes for comparing numerical values: `Strict` and `NonStrict`. These
     * modes are used to determine how strictly numerical values should be compared, particularly in cases where
     * different types of numerical values (e.g., integers and floating-point numbers) are involved.
     *
     * - `Strict`: Enforces strict equality between numbers, meaning that both the type and value must match exactly.
     * For example, `1` (an `Int`) and `1.0` (a `Double`) would not be considered equal in `Strict` mode.
     *
     * - `NonStrict`: Allows for more lenient comparison between numbers, focusing on value equality rather than type
     * equality. In `NonStrict` mode, `1` (an `Int`) and `1.0` (a `Double`) could be considered equal.
     */
    enum NumberEquality {

        /** Strict equality mode for numerical comparisons.
         *
         * In `Strict` mode, both the value and the type of the numbers must match exactly for them to be considered
         * equal. This mode is typically used when precise control over number comparisons is required, such as in
         * mathematical or financial calculations.
         */
        case Strict

        /** Non-strict equality mode for numerical comparisons.
         *
         * In `NonStrict` mode, the focus is on the value of the numbers rather than their types. This mode allows for
         * comparisons between different numerical types (e.g., `Int` and `Double`) as long as their values are
         * considered equal. It is often used in scenarios where flexibility in number comparison is desired.
         */
        case NonStrict
    }
}