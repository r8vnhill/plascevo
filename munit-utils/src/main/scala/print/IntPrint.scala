/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.munit
package print

/**
 * A printer for `Int` values that converts them to their string representation.
 *
 * The `IntPrint` object extends the `Print[Int]` trait and provides an implementation of the `print` method
 * specifically for `Int` values. This printer converts an integer value to its string representation and wraps it in a
 * `Printed` object.
 */
object IntPrint extends Print[Int] {

    /**
     * Converts an `Int` value to its string representation and wraps it in a `Printed` object.
     *
     * @param value The `Int` value to be printed.
     * @param level The indentation level for nested structures (not used in this implementation).
     * @return A `Printed` object containing the string representation of the `Int` value.
     */
    override def print(value: Int, level: Int): Printed = Printed(Some(value.toString))
}
