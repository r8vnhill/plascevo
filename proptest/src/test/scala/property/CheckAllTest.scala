/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property

import property.arbitrary.Ints.arbInt

class CheckAllTest extends CheckAllSuite {
    given config: PropTestConfig = PropTestConfig()

    test("Integers are non-negative") {
        checkAll(arbInt()) { i =>
            assert(i >= 0)
        }
    }
}
