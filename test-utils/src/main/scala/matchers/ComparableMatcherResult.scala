/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package matchers

/** A trait for representing the result of a comparison-based matcher.
 *
 * The `ComparableMatcherResult` trait extends the `MatcherResult` trait to include additional information about the
 * actual and expected values involved in the comparison. This is useful in scenarios where you need to provide more
 * detailed feedback about the result of a test or assertion, particularly when comparing two values.
 */
trait ComparableMatcherResult extends MatcherResult {

    /** The actual value produced by the test.
     *
     * This method returns the value that was actually produced or encountered during the test. It is used to provide
     * context when a test fails, showing what the test produced versus what was expected.
     *
     * @return The actual value as a string.
     */
    def actual: String

    /** The expected value that the test was comparing against.
     *
     * This method returns the value that was expected in the test. It is used in conjunction with `actual` to provide a
     * clear comparison between what was expected and what was actually produced.
     *
     * @return The expected value as a string.
     */
    def expected: String
}

/** Companion object for `ComparableMatcherResult` providing factory and extractor methods.
 *
 * The `ComparableMatcherResult` companion object provides methods to create instances of `ComparableMatcherResult` and
 * to extract its components for pattern matching.
 */
object ComparableMatcherResult {

    /** Creates a new `ComparableMatcherResult` instance.
     *
     * This method constructs a new instance of `ComparableMatcherResult`, taking in the test outcome, failure messages,
     * and the actual and expected values. It is typically used within matchers to generate a result that includes
     * detailed comparison information.
     *
     * @param testPassed            Indicates whether the test passed.
     * @param lazyFailureMessage    A lazily evaluated message describing the failure.
     * @param lazyNegatedFailureMessage A lazily evaluated message for when the test is expected to fail but passes.
     * @param actualValue           The actual value encountered in the test.
     * @param expectedValue         The value that was expected in the test.
     * @return A new `ComparableMatcherResult` instance.
     */
    def apply(
        testPassed: Boolean,
        lazyFailureMessage: => String,
        lazyNegatedFailureMessage: => String,
        actualValue: String,
        expectedValue: String
    ): ComparableMatcherResult = new ComparableMatcherResult {
        override def passed(): Boolean = testPassed

        override def failureMessage(): String = lazyFailureMessage

        override def negatedFailureMessage(): String = lazyNegatedFailureMessage

        override def actual: String = actualValue

        override def expected: String = expectedValue
    }

    /** Extracts the components of a `ComparableMatcherResult`.
     *
     * This method provides a way to deconstruct a `ComparableMatcherResult` into its components, making it easier to
     * pattern match on the results of comparisons in tests.
     *
     * @param result The `ComparableMatcherResult` instance to deconstruct.
     * @return An `Option` containing a tuple of the test outcome, failure messages, and the actual and expected values.
     */
    def unapply(result: ComparableMatcherResult): Option[(Boolean, String, String, String, String)] = {
        Some((result.passed(), result.failureMessage(), result.negatedFailureMessage(), result.actual, result.expected))
    }
}
