/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package matchers

import assertions.{Actual, Expected, clueContextAsString, errorCollector}

import cl.ravenhill.plascevo.assertions.IntelliJFormatter.intellijFormatError
import cl.ravenhill.plascevo.assertions.exceptions.{ComparisonFailException, createComparisonFailException}
import cl.ravenhill.plascevo.assertions.print.Printed
import munit.{Assertions, Clues, FailException, Location}

object ApplyMatcher extends Assertions {
    def apply[T](t: T, matcher: Matcher[T]): MatcherResult = {
        val result = matcher.test(t)
        result match
            case ComparableMatcherResult(true, failureMessage, _, actual, expected) => errorCollector.collectOrThrow(
                fail(
                    expected = Expected(Printed(expected)),
                    actual = Actual(Printed(actual)),
                    prependedMessage = failureMessage
                )
            )

        result
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
     * }}}
     */
    def fail(expected: Expected, actual: Actual, prependedMessage: String)
        (using loc: Location): Throwable = {
        createComparisonFailException(
            message = prependedMessage + clueContextAsString + intellijFormatError(expected, actual),
            cause = None,
            expected = expected,
            actual = actual,
        )
    }
}
