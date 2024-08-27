/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.munit.print

/** Trait representing a print operation that converts a value to a `Printed` representation.
 *
 * The `Print` trait defines a contract for classes that provide custom printing logic for different types of values.
 * Implementations of this trait should override the `print` method to provide a specific way of converting a value
 * into a `Printed` format, considering the given level of detail.
 */
trait Print {

    /** Converts a given value into a `Printed` representation.
     *
     * The `print` method should be implemented by subclasses to define how a specific value should be converted into
     * a `Printed` format. The `level` parameter can be used to control the depth or detail level of the printing.
     *
     * @param value The value to be printed.
     * @param level An integer representing the level of detail for the printing operation.
     * @return A `Printed` representation of the value.
     */
    def print(value: Any, level: Int): Printed
}

/** Companion object for the `Print` trait.
 *
 * The `Print` companion object provides utility methods and extension methods related to printing values. It includes
 * an extension method `print` that simplifies the process of printing a value by automatically selecting the
 * appropriate `Print` implementation based on the value's type.
 */
object Print {
    
    /**
     * Generates a `Printed` representation of a given value.
     *
     * The `printed` method takes a value of any type `T` and returns its `Printed` representation. The method handles
     * `null` values by delegating to `NullPrint`, and for non-null values, it delegates to the appropriate `Print`
     * instance for the type of the value.
     *
     * @param value The value to be printed. It can be of any type `T`.
     * @tparam T The type of the value being printed.
     * @return A `Printed` instance representing the formatted output of the value.
     */
    def printed[T](value: T): Printed = value match {
        case null => NullPrint.print(value, 0)
        case _ => printFor(value).print(value, 0)
    }

    /** Determines the appropriate `Print` implementation for a given value.
     *
     * This method is a placeholder for logic that identifies the correct `Print` implementation based on the type of
     * the provided value. The actual implementation of this method would need to be provided separately.
     *
     * @param value The value for which to determine the `Print` implementation.
     * @return The appropriate `Print` instance for the value's type.
     */
    def printFor(value: Any): Print = {
        // Implementation needed to return the correct Print instance based on the value's type
        ???
    }
}
