/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package matchers

/**
 * A trait that extends `MatcherResult` to include an optional error.
 *
 * `MatcherResultWithError` provides an additional layer of information on top of `MatcherResult` by including an
 * optional `Throwable` error. This is useful in scenarios where a matcher might need to capture an exception or error
 * that occurred during the evaluation of a match.
 */
trait MatcherResultWithError extends MatcherResult {

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

    /** Creates an instance of `MatcherResultWithError`.
     *
     * The `apply` method constructs a `MatcherResultWithError` with the provided error, match result, and functions 
     * that generate the failure messages. The failure messages are evaluated lazily, ensuring they are only computed 
     * when needed.
     *
     * @param error                   An optional `Throwable` representing an error that occurred during the match
     *                                evaluation.
     * @param matches                 A boolean indicating whether the matcher passed.
     * @param failureMessageFn        A function that generates the failure message based on the provided error.
     * @param negatedFailureMessageFn A function that generates the negated failure message based on the provided error.
     * @return An instance of `MatcherResultWithError` containing the provided values and logic.
     */
    def apply(
        error: Option[Throwable],
        matches: Boolean,
        failureMessageFn: Option[Throwable] => String,
        negatedFailureMessageFn: Option[Throwable] => String
    ): MatcherResultWithError = new MatcherResultWithError {

        /** The error encountered during the match evaluation. */
        override val error: Option[Throwable] = error

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

    /**
     * Extractor method for pattern matching on `MatcherResultWithError`.
     *
     * The `unapply` method is used to deconstruct an instance of `MatcherResultWithError` into its constituent parts, 
     * making it easier to use in pattern matching expressions. This method returns an `Option` containing a tuple with 
     * the error, match result, failure message, and negated failure message.
     *
     * @param result The `MatcherResultWithError` instance to be deconstructed.
     * @return An `Option` containing a tuple with:
     *         - `Option[Throwable]`: The optional error encountered during the match evaluation.
     *         - `Boolean`: A boolean indicating whether the matcher passed.
     *         - `String`: The failure message generated during the match evaluation.
     *         - `String`: The negated failure message generated during the match evaluation.
     */
    def unapply(result: MatcherResultWithError): Option[(Option[Throwable], Boolean, String, String)] = {
        Some((result.error, result.passed(), result.failureMessage(), result.negatedFailureMessage()))
    }
}
