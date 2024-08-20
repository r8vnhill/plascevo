package cl.ravenhill.plascevo
package utils

import matchers.beApproximatelyEqualTo
import utils.Numeric.roundUpToMultipleOf

import org.scalacheck.Gen

import scala.math.abs

class NumericTest extends AbstractPlascevoTest {
    "Comparing for approximate equality" - {
        "should be true if the difference between the values is less than the tolerance" in {
            forAll(nonNanDoublePairGen(Gen.chooseNum(-100_000_000.0, 100_000_000.0)).flatMap { case (a, b) =>
                Gen.chooseNum(abs(a - b), Double.MaxValue).filter(_ > abs(a - b)).map(tolerance => (a, b, tolerance))
            }) { case (a, b, tolerance) =>
                given Double = tolerance

                a should beApproximatelyEqualTo(b)
            }
        }

        "should be false if the difference between the values is greater than the tolerance" in {
            forAll(
                nonNanDoublePairGen(Gen.chooseNum(-100_000_000.0, 100_000_000.0))
                    .filter { case (a, b) => math.abs(a - b) > 0.0 }
                    .flatMap { case (a, b) =>
                        Gen.chooseNum(0.0, math.abs(a - b)).map(threshold => (a, b, threshold))
                    }
            ) { case (a, b, threshold) =>
                given Double = threshold

                a shouldNot beApproximatelyEqualTo(b)
            }
        }

        "should be false if the first value is NaN" in {
            forAll { (b: Double, tolerance: Double) =>
                given Double = tolerance

                Double.NaN shouldNot beApproximatelyEqualTo(b)
            }
        }

        "should be false if the second value is NaN" in {
            forAll { (a: Double, tolerance: Double) =>
                given Double = tolerance

                a shouldNot beApproximatelyEqualTo(Double.NaN)
            }
        }

        "should be true if both values are positive infinity" in {
            forAll { (tolerance: Double) =>
                given Double = tolerance

                Double.PositiveInfinity should beApproximatelyEqualTo(Double.PositiveInfinity)
            }
        }

        "should be true if both values are negative infinity" in {
            forAll { (tolerance: Double) =>
                given Double = tolerance

                Double.NegativeInfinity should beApproximatelyEqualTo(Double.NegativeInfinity)
            }
        }
    }

    "Rounding up to a multiple of an integer" - {
        // Define the test cases in a table
        val testCases = Table(
            ("n", "i", "expected"),
            (7, 3, 9), // General case: rounding up a number that is not a multiple
            (9, 3, 9), // Exact multiple: should return the same number
            (10, 5, 10), // Exact multiple: should return the same number
            (12, 5, 15), // General case: rounding up to the next multiple
            (7, 1, 7), // Multiplying by 1: should return the same number
            (7, 0, 7), // i is 0: should return the original number
            (0, 3, 0), // n is 0: should return 0
            (-7, 3, -6), // Negative number: rounding up to the next multiple
            (-10, 5, -10) // Negative exact multiple: should return the same number
        )

        "should round up to the nearest multiple of the specified integer" in {
            forAll(testCases) { (n, i, expected) =>
                n roundUpToMultipleOf i shouldEqual expected
            }
        }
    }
}
