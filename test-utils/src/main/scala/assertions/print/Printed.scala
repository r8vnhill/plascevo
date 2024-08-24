/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package assertions.print

/** A simple case class representing a printed value.
 *
 * The `Printed` case class is a lightweight container for a string value, typically used to represent text that has
 * been printed or is intended to be printed. It can be used in contexts where a clear and straightforward
 * representation of printed output is needed, such as logging, debugging, or display purposes.
 *
 * @param value The string value representing the printed content.
 */
case class Printed(value: String)
