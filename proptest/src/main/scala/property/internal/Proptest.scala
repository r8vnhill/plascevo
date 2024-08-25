/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.internal

import property.arbitrary.of.Generator
import property.{PropTestConfig, PropertyContext}

object Proptest {

    def apply[A](gen: Generator[A])(f: A => Unit)
        (using config: PropTestConfig): PropertyContext = ???
    
    def apply[A, B](genA: Generator[A], genB: Generator[B])
        (f: (A, B) => Unit)
        (using config: PropTestConfig): PropertyContext = ???
}
