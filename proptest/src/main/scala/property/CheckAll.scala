/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property

import property.arbitrary.of.Generator
import property.internal.Proptest

trait CheckAll {
    def checkAll[A](gen: Generator[A])(f: A => Unit)(using config: PropTestConfig): PropertyContext = Proptest(gen)(f)

    def checkAll[A, B](genA: Generator[A], genB: Generator[B])(f: (A, B) => Unit)
        (using config: PropTestConfig): PropertyContext =
        Proptest(genA, genB)(f)
}
