/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.checkall
package property

import munit.checkall
import munit.checkall.{CheckAllSuite, PropTestConfig}
import munit.checkall.arbitrary.Ints.arbInt

class CheckAllTest extends CheckAllSuite {
    given config: PropTestConfig = checkall.PropTestConfig()

    test("Integers are non-negative") {
        checkAll(arbInt()) { i =>
            assert(i >= 0)
        }
    }
}
