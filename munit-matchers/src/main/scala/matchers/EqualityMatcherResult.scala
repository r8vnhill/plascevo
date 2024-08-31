/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.matchers
package matchers

/**
 * Represents the result of an equality-based matcher operation.
 *
 * The `EqualityMatcherResult` trait extends `MatcherResult` to include additional information about the expected and
 * actual values involved in a comparison. This trait is typically used when implementing matchers that compare two
 * values for equality and need to report the specific values being compared in the event of a mismatch.
 */
trait EqualityMatcherResult extends MatcherResult {

    /**
     * The actual value that was compared during the equality check.
     *
     * @return An `Option` containing the actual value if present, or `None` if the actual value is not available.
     */
    def actual: Option[Any]

    /**
     * The expected value that the actual value was compared against during the equality check.
     *
     * @return An `Option` containing the expected value if present, or `None` if the expected value is not available.
     */
    def expected: Option[Any]
}

/**
 * Companion object for the `EqualityMatcherResult` trait, providing factory and extractor methods.
 *
 * The `EqualityMatcherResult` object provides a convenient way to create instances of `EqualityMatcherResult` using an
 * `apply` method and also allows pattern matching on `EqualityMatcherResult` instances via the `unapply` method.
 */
object EqualityMatcherResult {

    /**
     * Creates an instance of `EqualityMatcherResult`.
     *
     * The `apply` method constructs an `EqualityMatcherResult` with the provided match result, actual and expected
     * values, and lazy-loaded failure messages. This method is useful for creating instances in a clear and concise
     * manner.
     *
     * @param matches               A boolean indicating whether the equality check passed.
     * @param actualValue            The actual value that was compared.
     * @param expectedValue          The expected value that the actual value was compared against.
     * @param failureMessageFn       A lazy-loaded string that provides the failure message if the equality check fails.
     * @param negatedFailureMessageFn A lazy-loaded string that provides the negated failure message if the equality
     *                                check passes.
     * @return An instance of `EqualityMatcherResult` containing the provided values and logic.
     */
    def apply(
        matches: Boolean,
        actualValue: Option[Any],
        expectedValue: Option[Any],
        failureMessageFn: => String,
        negatedFailureMessageFn: => String
    ): EqualityMatcherResult = new EqualityMatcherResult {

        override def expected: Option[Any] = expectedValue

        override def actual: Option[Any] = actualValue

        override def negatedFailureMessage(): String = negatedFailureMessageFn

        override def failureMessage(): String = failureMessageFn

        override def passed(): Boolean = matches
    }

    /**
     * Extractor method for `EqualityMatcherResult`.
     *
     * The `unapply` method allows pattern matching on `EqualityMatcherResult` instances. It extracts the match result,
     * actual value, expected value, failure message, and negated failure message, making it easier to use in cases
     * where you need to deconstruct an `EqualityMatcherResult`.
     *
     * @param result The `EqualityMatcherResult` instance to be deconstructed.
     * @return An `Option` containing a tuple with the match result, actual value, expected value, failure message, and 
     *         negated failure message, or `None` if the input is not a valid `EqualityMatcherResult`.
     */
    def unapply(result: EqualityMatcherResult): Option[(Boolean, Option[Any], Option[Any], String, String)] = {
        Some((result.passed(), result.actual, result.expected, result.failureMessage(), result.negatedFailureMessage()))
    }
}

