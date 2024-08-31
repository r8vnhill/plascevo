/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.matchers
package matchers

import matchers.eq.EqualityMatcher

import cl.ravenhill.munit.collectors.ErrorCollector

trait ShouldMatchers {
    extension [A](actual: A) {
        /** Applies a matcher to the actual value using `ApplyMatcher`.
         *
         * The `should` method is an infix method that allows for more readable assertions. It takes a matcher as an
         * argument and applies it to the `actual` value, delegating the result handling to the `ApplyMatcher` utility.
         *
         * @param matcher The `Matcher[A]` to be applied to the `actual` value.
         */
        infix def should(matcher: Matcher[A]): Unit = ApplyMatcher(actual, matcher)

        /** Asserts that the actual value matches the expected value or satisfies a matcher.
         *
         * The `shouldBe` method is used to compare the `actual` value against an `expected` value. If the `expected`
         * value is a `Matcher`, the matcher is applied to the `actual` value using `ApplyMatcher`. Otherwise, a direct
         * comparison is performed using the `be` method.
         *
         * @param expected The expected value or a `Matcher` that the `actual` value should satisfy.
         */
        infix def shouldBe(expected: A): Unit = expected match
            case matcher: Matcher[?] => ApplyMatcher(actual, matcher.asInstanceOf[Matcher[A]])
            case _ => actual should be(expected)

        /** Creates a matcher to compare the actual value with the expected value.
         *
         * The `be` method is used to generate a `Matcher` that compares the `actual` value against the `expected`
         * value. This matcher can then be used in assertions to verify that the `actual` value meets the expectations.
         *
         * @param expected The value that the actual value is expected to match.
         * @return A `Matcher` that compares the `actual` value with the `expected` value.
         */
        def be(expected: A): Matcher[A] = equalityMatcher(expected)

        /** Creates an equality matcher for comparing the actual value with the expected value.
         *
         * The `equalityMatcher` method is used to generate a `Matcher` that compares an actual value with a specified
         * expected value. This matcher checks for equality between the actual and expected values and can be used in
         * assertions to verify that the actual value meets the expectations.
         *
         * @param expected The value that the actual value is expected to match.
         * @return A `Matcher` that compares the actual value with the expected value.
         */
        def equalityMatcher(expected: A): Matcher[A] = EqualityMatcher(expected)
    }
}
