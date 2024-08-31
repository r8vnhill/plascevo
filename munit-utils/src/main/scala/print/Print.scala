/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.munit
package print

import cl.ravenhill.composerr.Constrained.constrainedTo
import cl.ravenhill.composerr.constraints.option.BeNone
import cl.ravenhill.composerr.exceptions.CompositeException

/** Trait representing a print operation that converts a value to a `Printed` representation.
 *
 * The `Print` trait defines a contract for classes that provide custom printing logic for different types of values.
 * Implementations of this trait should override the `print` method to provide a specific way of converting a value
 * into a `Printed` format, considering the given level of detail.
 */
trait Print[-T] {

    /** Converts a given value into a `Printed` representation.
     *
     * The `print` method should be implemented by subclasses to define how a specific value should be converted into
     * a `Printed` format. The `level` parameter can be used to control the depth or detail level of the printing.
     *
     * @param value The value to be printed.
     * @param level An integer representing the level of detail for the printing operation.
     * @return A `Printed` representation of the value.
     */
    def print(value: T, level: Int): Printed
}

/** Companion object for the `Print` trait.
 *
 * The `Print` companion object provides utility methods and extension methods related to printing values. It includes
 * an extension method `print` that simplifies the process of printing a value by automatically selecting the
 * appropriate `Print` implementation based on the value's type.
 */
object Print {

    /**
     * Attempts to print a value using the appropriate printer and returns the result wrapped in a `Try`.
     *
     * The `printed` method tries to find a registered printer for the given value and then uses it to print the value.
     * If the value is `null`, it uses a predefined `NullPrint` printer. If no printer can be found for the value's
     * type, the method returns a `Try` containing a `CompositeException` that wraps an `OptionConstraintException`.
     *
     * @param value The value to be printed. This value can be of any type, and it is matched against registered
     *              printers.
     * @tparam T The type of the value being printed.
     * @return A `Try[Printed]` containing the printed representation of the value. If no suitable printer is found,
     *         the `Try` will contain a `CompositeException` that wraps an `OptionConstraintException`.
     */
    def printed[T](value: T): Either[CompositeException, Printed] =
        value match {
            case null => Right(NullPrint.print(value, 0))
            case _ =>
                val print = printFor(value)
                print.constrainedTo {
                    s"Could not find a printer for value of type ${value.getClass.getName}" | {
                        print mustNot BeNone
                    }
                } match
                    case Left(e) => Left(e)
                    case Right(p) => Right(p.get.print(value, 0))
        }

    private def printFor[T](value: T): Option[Print[T]] = {
        Printers.all
            .find { case (k, _) => k.isInstance(value) }
            .map { case (_, v) => v.asInstanceOf[Print[T]] }
    }
}
