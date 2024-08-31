/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.matchers
package matchers

import cl.ravenhill.munit.assertions.{Actual, Expected}

import scala.util.Try

/**
 * A trait that extends `MatcherResult` to include an optional error.
 *
 * `MatcherResultWithError` provides an additional layer of information on top of `MatcherResult` by including an
 * optional `Throwable` error. This is useful in scenarios where a matcher might need to capture an exception or error
 * that occurred during the evaluation of a match.
 */
trait MatcherResultWithError extends MatcherResult {

    /** The actual value that was evaluated during the match. */
    val actualValue: Actual

    /** The expected value that was used in the match. */
    val expectedValue: Expected

    /** An optional error that occurred during the evaluation of the matcher.
     *
     * The `error` field contains an `Option` wrapping a `Throwable`. This can be used to store any exception or error
     * that was encountered during the matching process, allowing it to be included in the match result.
     *
     * By default, the `error` is set to `None`, indicating that no error was encountered.
     */
    val error: Option[Throwable] = None
}

/**
 * Companion object for the `MatcherResultWithError` trait, providing a factory method.
 *
 * The `MatcherResultWithError` object provides a convenient way to create instances of `MatcherResultWithError`,
 * allowing you to specify the error, match result, and lazy-loaded failure messages.
 */
object MatcherResultWithError {

    def apply(
        cause: Option[Throwable],
        matches: Boolean,
        actual: Actual,
        expected: Expected,
        failureMessageFn: Option[Throwable] => String,
        negatedFailureMessageFn: Option[Throwable] => String
    ): MatcherResultWithError = new MatcherResultWithError {

        /** The actual value that was evaluated during the match. */
        override val actualValue: Actual = actual

        /** The expected value that was used in the match. */
        override val expectedValue: Expected = expected

        /** The error encountered during the match evaluation. */
        override val error: Option[Throwable] = cause

        /** Returns the failure message, generated based on the provided error.
         *
         * This method evaluates the `failureMessageFn` function, passing the error as a parameter to produce the 
         * failure message.
         *
         * @return The failure message as a `String`.
         */
        override def failureMessage(): String = failureMessageFn(error)

        /** Returns the negated failure message, generated based on the provided error.
         *
         * This method evaluates the `negatedFailureMessageFn` function, passing the error as a parameter to produce the 
         * negated failure message.
         *
         * @return The negated failure message as a `String`.
         */
        override def negatedFailureMessage(): String = negatedFailureMessageFn(error)

        /** Indicates whether the matcher passed.
         *
         * @return `true` if the matcher passed, `false` otherwise.
         */
        override def passed(): Boolean = matches
    }

    def unapply(
        result: MatcherResultWithError
    ): Option[(Option[Throwable], Boolean, Actual, Expected, String, String)] =
        Some(
            (
                result.error,
                result.passed(),
                result.actualValue,
                result.expectedValue,
                result.failureMessage(),
                result.negatedFailureMessage()
            )
        )
}
