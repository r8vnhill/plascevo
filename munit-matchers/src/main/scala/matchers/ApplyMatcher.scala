/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.matchers
package matchers

import cl.ravenhill.munit.Exceptions.comparisonFailWithCause
import cl.ravenhill.munit.assertions.IntelliJFormatter.{intelliJFormatError, intelliJFormatErrorWithType}
import cl.ravenhill.munit.assertions.exceptions.createComparisonFailException
import cl.ravenhill.munit.assertions.{Actual, ActualWithType, Expected, ExpectedWithType}
import cl.ravenhill.munit.collectors.ErrorCollector.clueContextAsString
import cl.ravenhill.munit.collectors.{ErrorCollector, ThreadLocalErrorCollector}
import cl.ravenhill.munit.print.Printed
import munit.*

import scala.util.Try

/** Utility object for applying matchers and handling assertion failures.
 *
 * The `ApplyMatcher` object provides methods to apply matchers to values and handle the resulting assertion failures
 * in a controlled manner. It supports comparison failures, generic failures, and errors with specific causes, ensuring
 * that detailed information is provided when a test fails. This object is designed to work within munit tests,
 * particularly in scenarios where detailed error reporting and context clues are essential.
 */
object ApplyMatcher extends Assertions {

    def apply[T](t: T, matcher: Matcher[T])
        (using errorCollector: ErrorCollector = ThreadLocalErrorCollector()): MatcherResult = {
        val result = matcher.apply(t)
        (result.passed(), result) match {
            case (false, ComparableMatcherResult(true, failureMessage, _, actual, expected)) =>
                handleComparisonFailure(failureMessage, actual, expected)

            case (false, EqualityMatcherResult(true, actual, expected, failureMessage, _)) =>
                handleComparisonFailure(failureMessage, actual, expected)

            case (false, MatcherResultWithError(cause, _, actual, expected, failureMessage, _)) =>
                handleErrorWithCause(cause, Option(actual.value), Option(expected.value), failureMessage)

            case (false, _) =>
                handleGenericFailure(result.failureMessage(), None, None)
            case _ =>
        }
        result
    }

    /** Handles a comparison failure by creating and throwing or collecting an appropriate error.
     *
     * The `handleComparisonFailure` method is used to manage situations where a comparison between expected and actual
     * values fails. It constructs an error message incorporating the failure message, the actual value, and the
     * expected value, and then processes the error based on the current error collection mode.
     *
     * @param failureMessage A message describing the nature of the comparison failure. This message will be included in
     *                       the final error message.
     * @param actual         The actual value obtained during the comparison, represented as an `Option[Any]`.
     * @param expected       The expected value against which the actual value was compared, represented as an
     *                       `Option[Any]`.
     * @param loc            The location where the failure occurred, implicitly provided. This is used to annotate the
     *                       failure with the specific location in the code where the comparison failure happened.
     * @param errorCollector The error collector used to manage the error collection mode and handle the resulting
     *                       errors.
     */
    private def handleComparisonFailure(
        failureMessage: String,
        actual: Option[Any],
        expected: Option[Any]
    )(using loc: Location, errorCollector: ErrorCollector): Unit = {
        errorCollector.collectOrThrow(
            fail(
                expected = Expected(Printed(expected)),
                actual = Actual(Printed(actual)),
                prependedMessage = s"$failureMessage\n"
            )
        )
    }

    /**
     * Handles an error by either collecting or throwing it, considering an optional cause.
     *
     * The `handleErrorWithCause` method processes an error in the context of a comparison between an actual and
     * expected value. It checks if an optional cause (`Throwable`) is provided. If the cause exists, it collects or
     * throws it using the `ErrorCollector`. If no cause is provided, it generates a `ComparisonFailException` using the
     * provided failure message, actual value, and expected value, and then collects or throws this exception.
     *
     * @param cause          An optional `Throwable` representing the cause of the error. If present, this cause will be
     *                       handled by the `ErrorCollector`.
     * @param actual         An optional value representing the actual result obtained during the comparison.
     * @param expected       An optional value representing the expected result that the actual value was compared
     *                       against.
     * @param failureMessage A descriptive message indicating the nature of the failure.
     * @param loc            The `Location` in the code where the failure occurred, providing context.
     * @param errorCollector The `ErrorCollector` used to gather and report errors during the test. Depending on its
     *                       configuration, it may either collect the error for later reporting or throw it immediately.
     */
    private def handleErrorWithCause(
        cause: Option[Throwable],
        actual: Option[?],
        expected: Option[?],
        failureMessage: String
    )(using loc: Location, errorCollector: ErrorCollector): Unit = errorCollector.collectOrThrow(
        cause.getOrElse(failure(failureMessage, actual, expected))
    )

    /**
     * Handles a generic failure by collecting or throwing a `ComparisonFailException`.
     *
     * The `handleGenericFailure` method is responsible for handling a failure that occurs when the actual result does
     * not match the expected result in a test or comparison. It creates a `ComparisonFailException` using the provided
     * failure message, actual value, and expected value, and then either collects the exception using the
     * `ErrorCollector` or throws it, depending on the configuration of the `ErrorCollector`.
     *
     * @param failureMessage A descriptive message indicating the nature of the failure.
     * @param actual         An optional value representing the actual result obtained during the comparison.
     * @param expected       An optional value representing the expected result that the actual value was compared
     *                       against.
     * @param loc            The `Location` in the code where the failure occurred, providing context.
     * @param errorCollector The `ErrorCollector` used to gather and report errors during the test. Depending on its
     *                       configuration, it may either collect the error for later reporting or throw it immediately.
     */
    private def handleGenericFailure(
        failureMessage: String,
        actual: Option[?],
        expected: Option[?]
    )(
        using
        loc: Location,
        errorCollector: ErrorCollector
    ): Unit = errorCollector.collectOrThrow(failure(failureMessage, actual, expected))

    /**
     * Creates and returns a `Throwable` representing a comparison failure between an expected and actual value.
     *
     * The `fail` method is used to generate a failure exception when the actual value does not match the expected
     * value. It combines a prepended message, contextual clues, and a formatted error message to provide detailed
     * information about the failure. This method is typically used within assertions to signal test failures.
     *
     * @param expected         The expected value, wrapped in an `Expected` instance.
     * @param actual           The actual value, wrapped in an `Actual` instance.
     * @param prependedMessage A custom message to prepend to the failure details. This message is typically used to
     *                         provide additional context or information about the failure.
     * @param loc              An implicit `Location` parameter representing the location in the source code where the
     *                         failure occurred. This is automatically provided by the compiler and does not need to be
     *                         specified manually.
     * @return A `Throwable` representing the failure, including detailed information about the comparison that failed.
     */
    def fail(expected: Expected, actual: Actual, prependedMessage: String)
        (using loc: Location, errorCollector: ErrorCollector): ComparisonFailException = createComparisonFailException(
        message = prependedMessage + clueContextAsString + intelliJFormatError(expected, actual),
        cause = None,
        expected = expected,
        actual = actual,
    )

    /**
     * Creates a `ComparisonFailException` with a specified failure message and comparison details.
     *
     * The `failure` method constructs a `ComparisonFailException` using the provided failure message, actual value,
     * and expected value. This exception is used to signal that a comparison between `actual` and `expected` values
     * has failed. The method delegates the creation of the exception to the `comparisonFailWithCause` method,
     * which includes additional contextual information and error collection.
     *
     * @param failureMessage A descriptive message indicating the nature of the failure.
     * @param actual         An optional value representing the actual result obtained during the comparison.
     * @param expected       An optional value representing the expected result that the actual value was compared
     *                       against.
     * @param loc            The `Location` in the code where the failure occurred, providing context. This parameter is
     *                       provided implicitly using Scala's `using` keyword.
     * @param errorCollector The `ErrorCollector` used to gather and report errors during the test. This parameter is
     *                       provided implicitly using Scala's `using` keyword.
     * @return A `ComparisonFailException` containing the failure details, including the failure message, the actual and
     *         expected values, and the location of the failure.
     */
    private def failure(
        failureMessage: String,
        actual: Option[?],
        expected: Option[?]
    )(
        using
        loc: Location,
        errorCollector: ErrorCollector
    ): ComparisonFailException = comparisonFailWithCause(failureMessage, actual, expected, None)

    def failureWithTypeInformation(
        expected: ExpectedWithType,
        actual: ActualWithType,
        prependMessage: String = ""
    )(using loc: Location, errorCollector: ErrorCollector): ComparisonFailException = {
        if (actual.value.typeInfo == expected.value.typeInfo) {
            createComparisonFailException(
                prependMessage + clueContextAsString + intelliJFormatError(expected.toExpected, actual.toActual),
                None,
                expected.toExpected,
                actual.toActual
            )
        } else {
            createComparisonFailException(
                message = prependMessage + clueContextAsString + intelliJFormatErrorWithType(expected, actual),
                cause = None,
                expected = expected.toExpected,
                actual = actual.toActual
            )
        }
    }
}
