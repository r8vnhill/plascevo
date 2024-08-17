package cl.ravenhill.plascevo
package matchers

import cl.ravenhill.plascevo.mixins.Verifiable
import org.scalatest.matchers.{MatchResult, Matcher}

/** Provides a custom matcher to verify the validity of `Verifiable` instances in tests.
 *
 * The `beValid` matcher is used to assert that a `Verifiable` instance successfully passes its `verify` method. It
 * returns a `MatchResult` indicating whether the verification was successful or not. This matcher is useful in tests
 * where the validity of objects implementing the `Verifiable` trait needs to be confirmed.
 *
 * @return A `Matcher[Verifiable]` that checks whether the `Verifiable` instance is valid.
 */
def beValid: Matcher[Verifiable] = (left: Verifiable) => {
    val isValid = left.verify()
    MatchResult(
        isValid,
        s"${left.toString} was not valid",
        s"${left.toString} was valid"
    )
}
