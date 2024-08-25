/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property

import property.arbitrary.Ints.arbInt

class CheckAllTest extends munit.FunSuite with CheckAll {
    test("checkAll should run a property test") {
        given config: PropTestConfig = PropTestConfig()
        checkAll(arbInt()) { _ => }
    }
}