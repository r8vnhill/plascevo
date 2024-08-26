/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property

import property.arbitrary.Ints.arbInt

class CheckAllTest extends CheckAllSuite {
    given config: PropTestConfig = PropTestConfig()

    checkAll("Integers")(arbInt()) { i =>
        assert(i >= 0)
    }
}
