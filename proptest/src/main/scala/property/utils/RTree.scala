/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.utils

case class RTree[+A](value: () => A, children: () => Seq[RTree[A]] = () => Seq.empty)
