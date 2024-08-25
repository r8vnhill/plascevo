/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property

object SysPropEnv {
    def sysprop[T](key: String, default: T)(converter: String => T): T = ???
}
