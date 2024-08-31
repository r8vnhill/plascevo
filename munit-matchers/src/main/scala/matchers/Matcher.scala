/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.matchers
package matchers

import cl.ravenhill.munit.collectors.ErrorCollector

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
    def apply(value: T)(using errorCollector: ErrorCollector): MatcherResult
}
