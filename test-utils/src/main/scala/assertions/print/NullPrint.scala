/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package assertions.print

/** Object representing a `Print` implementation for `null` values.
 *
 * The `NullPrint` object provides a specific implementation of the `Print` trait that handles the printing of `null`
 * values. When the `print` method is called on a `null` value, this object returns a `Printed` instance containing the
 * string `"<null>"`, indicating that the value was `null`.
 *
 * This object is typically used internally by the `Print` companion object when handling `null` values during the
 * printing process.
 */
object NullPrint extends Print {

    /** Converts a `null` value into a `Printed` representation.
     *
     * The `print` method in `NullPrint` returns a `Printed` instance containing the string `"<null>"`, indicating that
     * the value being printed was `null`. The `level` parameter is ignored in this implementation since `null` values
     * do not require any additional detail.
     *
     * @param value The value to be printed, which should be `null`.
     * @param level An integer representing the level of detail for the printing operation. This parameter is ignored in
     *              this implementation.
     * @return A `Printed` instance containing the string `"<null>"`.
     */
    override def print(value: Any, level: Int): Printed = Printed(Some("<null>"))
}
