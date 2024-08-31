/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.matchers
package assertions

import matchers.ShouldMatchers

import cl.ravenhill.munit.assertions.Actual
import cl.ravenhill.munit.print.Printed

class ActualTest extends munit.FunSuite with ShouldMatchers {
    test("Actual should wrap a value") {
        val actual = Actual(Printed(Some("value")))
        actual.value shouldBe Printed(Some("value"))
    }
}
