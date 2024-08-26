/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.checkall
package property

import arbitrary.Ints.arbInt

class CheckAllTest extends CheckAllSuite {
    given config: PropTestConfig = PropTestConfig()

    test("Integers are non-negative") {
        checkAll(arbInt()) { i =>
            assert(i >= 0)
        }
    }
}
