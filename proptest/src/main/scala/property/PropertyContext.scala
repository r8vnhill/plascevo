/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property

class PropertyContext(using val config: PropTestConfig = PropTestConfig()) {
    private var _evaluations: Int = 0

    def evaluations: Int = _evaluations

    def onSuccess(args: Int, random: RandomSource): Unit = ???
}
