/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package assertions.print

/**
 * A case class that encapsulates a value along with its type information.
 *
 * The `PrintedWithType` case class stores a value as a `String` along with its corresponding type information as
 * another `String`. This class is useful for scenarios where both the value and its type need to be tracked and
 * processed together.
 *
 * @param value    The string representation of the value.
 * @param typeInfo The string representation of the type information associated with the value.
 */
final case class PrintedWithType(value: String, typeInfo: String)

object PrintedWithType {

    extension (value: String) {
        /**
         * Extension method to convert a `String` value into a `Printed` instance.
         *
         * This extension method adds a `printed` method to the `String` class, allowing any `String` to be easily 
         * converted into a `Printed` instance. This is particularly useful when working with `PrintedWithType`
         * instances where only the value (without type information) needs to be accessed as a `Printed` object.
         * @return A `Printed` instance containing the string value.
         */
        def printed: Printed = Printed(value)
    }
}
