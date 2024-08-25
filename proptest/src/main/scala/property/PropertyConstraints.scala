/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property

trait PropertyConstraints

object PropertyConstraints {
    def iterations(k: Int): PropertyContext => Boolean = (context: PropertyContext) => context.evaluations < k
}
