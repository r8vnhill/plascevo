/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property

import property.arbitrary.generators.Generator
import property.internal.PropertyTest

import cl.ravenhill.plascevo.property.context.PropertyContext

trait CheckAll {
    def checkAll[A](gen: Generator[A])(f: A => Unit)(using config: PropTestConfig)
        (using context: PropertyContext = PropertyContext()): PropertyContext =
        PropertyTest(gen)(f)

    def checkAll[A, B](genA: Generator[A], genB: Generator[B])(f: (A, B) => Unit)
        (using config: PropTestConfig): PropertyContext =
        PropertyTest(genA, genB)(f)
}
