/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.matchers
package matchers

import scala.util.chaining.scalaUtilChainingOps

/** Represents a general matcher that can be used to test a value against certain criteria.
 *
 * The `Matcher` trait defines a standard interface for performing tests on values of type `T`. It returns a
 * `MatcherResult` that encapsulates whether the test passed or failed, along with the appropriate failure messages.
 *
 * @tparam T The type of value that the matcher can test.
 */
trait Matcher[-T] {

    /** Tests the given value against the matcher's criteria.
     *
     * This method applies the matcher's logic to the input value and returns a `MatcherResult` indicating whether the
     * test passed or failed, along with the relevant failure messages.
     *
     * @param value The value to be tested by the matcher.
     * @return A `MatcherResult` representing the outcome of the test.
     */
    def apply(value: T): MatcherResult

    /** Transforms the input type of the matcher using a function, enabling the matcher to be applied to different types.
     *
     * The `contraMap` method allows you to adapt the matcher to test values of a different type `U`. It achieves this 
     * by applying the function `f` to the input value, converting it to the type `T` that the original matcher can handle.
     *
     * @param f A function that transforms a value of type `U` into a value of type `T`.
     * @tparam U The new input type that the matcher can handle after transformation.
     * @return A new `Matcher[U]` that tests values of type `U` by first converting them to type `T`.
     *
     * @example
     * {{{
     * val intMatcher: Matcher[Int] = ???
     * val stringLengthMatcher: Matcher[String] = intMatcher.contraMap(_.length)
     * }}}
     */
    infix def contraMap[U](f: U => T): Matcher[U] = (value: U) => apply(f(value))

    /** Inverts the result of the matcher, turning a pass into a fail and vice versa.
     *
     * The `invert` method creates a new matcher that negates the result of the original matcher. If the original matcher 
     * passes, the inverted matcher will fail, and if the original matcher fails, the inverted matcher will pass.
     *
     * @return A new `Matcher[T]` that inverts the result of the original matcher.
     *
     * @example
     * {{{
     * val positiveMatcher: Matcher[Int] = ???
     * val nonPositiveMatcher: Matcher[Int] = positiveMatcher.invert
     * }}}
     */
    def invert: Matcher[T] = (value: T) => {
        apply(value).tap { result =>
            MatcherResult(
                !result.passed(),
                result.failureMessage(),
                result.negatedFailureMessage()
            )
        }
    }
}
