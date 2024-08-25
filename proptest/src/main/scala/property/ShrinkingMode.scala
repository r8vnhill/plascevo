/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property

sealed trait ShrinkingMode

case class BoundedShrinking(maxSize: Int) extends ShrinkingMode
