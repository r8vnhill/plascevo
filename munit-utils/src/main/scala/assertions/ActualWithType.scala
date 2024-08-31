/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.munit.assertions

import cl.ravenhill.munit.print.PrintedWithType.printed
import cl.ravenhill.munit.print.PrintedWithType

/**
 * A wrapper class that represents an actual value along with its type information.
 *
 * The `ActualWithType` case class encapsulates a value of type `PrintedWithType`. This class is used to maintain both
 * the value and its type, providing a way to convert this representation to a simpler `Actual` instance that contains
 * only the printed value without the type information.
 *
 * @param value The `PrintedWithType` instance that contains both the value and its type information.
 */
final case class ActualWithType(value: PrintedWithType) {

    /**
     * Converts this `ActualWithType` instance to an `Actual` instance.
     *
     * The `toActual` method extracts the printed value from the encapsulated `PrintedWithType` instance and returns it
     * as a new `Actual` instance. This is useful when only the value, without type information, is needed.
     *
     * @return An `Actual` instance containing the printed value.
     */
    def toActual: Actual = Actual(value.value.printed)
}
