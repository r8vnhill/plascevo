/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo

import matchers.ShouldMatchers

import munit.FunSuite

class MunitTest extends FunSuite with ShouldMatchers {
    test("A test") {
        assertEquals(1, 2)
        2 shouldBe 1
    }
}
