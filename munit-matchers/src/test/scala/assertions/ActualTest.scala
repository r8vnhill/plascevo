/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package assertions
import assertions.print.Printed
import matchers.ShouldMatchers

class ActualTest extends munit.FunSuite with ShouldMatchers {
    test("Actual should wrap a value") {
        val actual = Actual(Printed(Some("value")))
        actual.value shouldBe Printed(Some("value"))
    }
}
