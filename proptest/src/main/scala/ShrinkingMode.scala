/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.checkall

sealed trait ShrinkingMode

case class BoundedShrinking(maxSize: Int) extends ShrinkingMode
