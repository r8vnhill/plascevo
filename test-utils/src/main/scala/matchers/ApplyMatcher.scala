/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package matchers

def applyMatcher[T](t: T, matcher: Matcher[T]): MatcherResult = {
    val result = matcher.test(t)
    result match
        case ComparableMatcherResult(true, failureMessage, _, actual, expected) =>

    result
}
