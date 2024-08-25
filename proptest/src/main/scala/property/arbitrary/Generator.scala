/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.arbitrary

sealed trait Generator[+T]

trait Arbitrary[+T] extends Generator[T]
