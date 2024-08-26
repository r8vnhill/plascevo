/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package matchers

/** Represents the result of a matcher test, encapsulating whether the test passed or failed, along with messages for
 * both failure and negated failure scenarios.
 *
 * The `MatcherResult` trait provides a standardized way to represent the outcome of a matcher in a test framework. It
 * includes methods to determine if the test passed, and to retrieve the failure messages when the test either fails or
 * fails to fail.
 */
trait MatcherResult {

    /** Indicates whether the test passed.
     *
     * @return `true` if the test passed, `false` otherwise.
     */
    def passed(): Boolean

    /** Provides the failure message when the test does not pass.
     *
     * This message is typically used to explain why the test failed when `passed` is `false`.
     *
     * @return A string describing the reason for the test failure.
     */
    def failureMessage(): String

    /** Provides the failure message when the test was expected to fail but passed.
     *
     * This message is used in scenarios where a test is expected to fail, but it unexpectedly passes.
     *
     * @return A string describing the reason for the failure to fail.
     */
    def negatedFailureMessage(): String
}

/** Companion object for creating instances of [[MatcherResult]].
 *
 * The `MatcherResult` object provides a factory method for creating `MatcherResult` instances. It allows lazy
 * evaluation of failure messages to avoid unnecessary computation when the test passes.
 */
object MatcherResult {

    /** Creates a new `MatcherResult` instance.
     *
     * @param testPassed                Indicates whether the test passed (`true`) or failed (`false`).
     * @param lazyFailureMessage        A lazily evaluated message to describe the reason for test failure.
     * @param lazyNegatedFailureMessage A lazily evaluated message to describe the reason for the failure 
     *                                  when the test was expected to fail but passed.
     * @return A new instance of `MatcherResult`.
     */
    def apply(
        testPassed: Boolean,
        lazyFailureMessage: => String,
        lazyNegatedFailureMessage: => String
    ): MatcherResult = new MatcherResult {
        override def passed(): Boolean = testPassed

        override def failureMessage(): String = lazyFailureMessage

        override def negatedFailureMessage(): String = lazyNegatedFailureMessage
    }
}
