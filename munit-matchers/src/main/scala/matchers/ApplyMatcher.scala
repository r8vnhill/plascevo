/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.matchers
package matchers

import munit.{Assertions, Location}

/** Utility object for applying matchers and handling assertion failures.
 *
 * The `ApplyMatcher` object provides methods to apply matchers to values and handle the resulting assertion failures
 * in a controlled manner. It supports comparison failures, generic failures, and errors with specific causes, ensuring
 * that detailed information is provided when a test fails. This object is designed to work within munit tests,
 * particularly in scenarios where detailed error reporting and context clues are essential.
 */
object ApplyMatcher extends Assertions {

    /** Applies a matcher to a value and handles any resulting failures.
     *
     * The `apply` method evaluates a given value `t` against a specified matcher, producing a `MatcherResult`.
     * Depending on the result, it handles potential comparison or matching failures by invoking the appropriate error
     * handling functions.
     *
     * @param t       The value of type `T` to be tested against the matcher.
     * @param matcher The `Matcher[T]` instance used to evaluate the value `t`.
     * @tparam T The type of the value being tested.
     * @return The `MatcherResult` produced by testing the value `t` with the matcher.
     * @throws CompositeException If an error is collected during the comparison or matching, depending on the error
     *                            collection mode.
     * @throws Throwable          If the error collection mode is hard, the failure is thrown immediately.
     */
    def apply[T](t: T, matcher: Matcher[T]): MatcherResult = {
        val result = matcher.apply(t)

        (result.passed(), result) match {
            case (false, ComparableMatcherResult(true, failureMessage, _, actual, expected)) =>
                handleComparisonFailure(failureMessage, actual, expected)

            case (false, EqualityMatcherResult(true, actual, expected, failureMessage, _)) =>
                handleComparisonFailure(failureMessage, actual, expected)

            case (false, MatcherResultWithError(cause, true, failureMessage, _)) =>
                handleErrorWithCause(cause, failureMessage)

            case (false, _) =>
                handleGenericFailure(result.failureMessage())
            case _ =>
        }w

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
     * @param expected       The expected value against which the actual value was compared, represented as an `Option[Any]`.
     * @param loc            The location where the failure occurred, implicitly provided. This is used to annotate the failure
     *                       with the specific location in the code where the comparison failure happened.
     */
    private def handleComparisonFailure(
        failureMessage: String,
        actual: Option[Any],
        expected: Option[Any]
    )(using loc: Location): Unit = {
        errorCollector.collectOrThrow(
            fail(
                expected = Expected(Printed(expected)),
                actual = Actual(Printed(actual)),
                prependedMessage = s"$failureMessage\n"
            )
        )
    }

    /** Handles errors by either using the provided cause or creating a new failure with a message.
     *
     * The `handleErrorWithCause` method is responsible for handling errors that may have an associated cause. If a
     * cause is provided, it will be used directly; otherwise, a new failure `Throwable` is created using the provided
     * failure message. The resulting error is then either collected or thrown based on the current error collection
     * mode.
     *
     * @param cause          An optional `Throwable` representing the cause of the failure. If provided, this will be used as the
     *                       error to be handled.
     * @param failureMessage The message describing the failure, used to create a new failure if the cause is not
     *                       provided.
     * @param loc            The location where the failure occurred, implicitly provided. This is used to annotate the failure
     *                       with the specific location in the code where the error happened.
     */
    private def handleErrorWithCause(
        cause: Option[Throwable],
        failureMessage: String
    )(using loc: Location): Unit = {
        val error = cause.getOrElse(failure(failureMessage))
        errorCollector.collectOrThrow(error)
    }

    /** Handles generic failures by collecting or throwing an error.
     *
     * The `handleGenericFailure` method is responsible for dealing with failures that are not specific to any
     * particular type of `MatcherResult`. It constructs a failure `Throwable` using the provided failure message and
     * either collects or throws it based on the current error collection mode.
     *
     * @param failureMessage The message describing the failure.
     * @param loc            The location where the failure occurred, implicitly provided. This is used to annotate the failure with
     *                       the specific location in the code where the error happened.
     */
    private def handleGenericFailure(failureMessage: String)(using loc: Location): Unit = {
        errorCollector.collectOrThrow(failure(failureMessage))
    }

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
     *         }}}
     */
    def fail(expected: Expected, actual: Actual, prependedMessage: String)
        (using loc: Location): Throwable = {
        createComparisonFailException(
            message = prependedMessage + clueContextAsString + intelliJFormatError(expected, actual),
            cause = None,
            expected = expected,
            actual = actual,
        )
    }

    /**
     * Creates a `Throwable` representing a failure with a specified message.
     *
     * The `failure` method generates a `Throwable` with the given failure message and the current location context.
     * It uses the `failureWithCause` method internally, passing `None` as the cause since no specific cause is
     * provided.
     *
     * @param failureMessage A `String` representing the failure message to be included in the `Throwable`.
     * @param loc            An implicit `Location` parameter that provides the location context of the failure.
     * @return A `Throwable` representing the failure, containing the provided message and location context.
     */
    def failure(failureMessage: String)(using loc: Location): Throwable =
        failureWithCause(failureMessage, None)

    def failureWithTypeInformation(
        expected: ExpectedWithType,
        actual: ActualWithType,
        prependMessage: String = ""
    )(using loc: Location): Throwable = {
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
