package cl.ravenhill.plascevo
package matchers

import utils.Numeric.{!=~, =~}

import org.scalatest.matchers.{MatchResult, Matcher}

/**
 * Creates a matcher that checks if a `Double` value is approximately equal to an expected value within a given
 * threshold.
 *
 * This matcher is useful for comparing floating-point numbers where exact equality is difficult due to precision
 * issues. The equality comparison is performed using an implicit `equalityThreshold` value, which defines the
 * acceptable range of difference between the expected and actual values.
 *
 * @param expected The expected `Double` value to compare against.
 * @param equalityThreshold An implicit `Double` value that specifies the threshold for considering two doubles
 *                          approximately equal.
 * @return A `Matcher[Double]` that matches if the actual value is approximately equal to the expected value within the
 *         threshold.
 */
def beApproximatelyEqualTo(expected: Double)(using equalityThreshold: Double): Matcher[Double] = (left: Double) => {
    val areEqual = left =~ expected
    MatchResult(
        areEqual,
        s"$left was not approximately equal to $expected",
        s"$left was approximately equal to $expected"
    )
}

/**
 * Creates a matcher that checks if a `Double` value is not approximately equal to an expected value within a given
 * threshold.
 *
 * This matcher is useful for cases where you want to ensure that two floating-point numbers are sufficiently different
 * from each other, taking into account potential precision issues inherent in floating-point arithmetic. The comparison
 * is performed using an implicit `equalityThreshold` value, which defines the acceptable range of difference for two
 * values to be considered not approximately equal.
 *
 * @param expected The expected `Double` value to compare against.
 * @param equalityThreshold An implicit `Double` value that specifies the threshold for considering two doubles not
 *                          approximately equal.
 * @return A `Matcher[Double]` that matches if the actual value is not approximately equal to the expected value within
 *         the threshold.
 */
def notBeApproximatelyEqualTo(expected: Double)(using equalityThreshold: Double): Matcher[Double] = (left: Double) => {
    val areNotEqual = left !=~ expected
    MatchResult(
        areNotEqual,
        s"$left was approximately equal to $expected",
        s"$left was not approximately equal to $expected"
    )
}
