/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package assertions

import assertions.print.PrintedWithType
import assertions.print.PrintedWithType.printed

/**
 * A wrapper class that represents an expected value along with its type information.
 *
 * The `ExpectedWithType` case class encapsulates a value of type `PrintedWithType`. This class is used to maintain both
 * the value and its type, providing a way to convert this representation to a simpler `Expected` instance that contains
 * only the printed value without the type information.
 *
 * @param value The `PrintedWithType` instance that contains both the value and its type information.
 */
final case class ExpectedWithType(value: PrintedWithType) {

    /**
     * Converts this `ExpectedWithType` instance to an `Expected` instance.
     *
     * The `toExpected` method extracts the printed value from the encapsulated `PrintedWithType` instance and returns
     * it as a new `Expected` instance. This is useful when only the value, without type information, is needed.
     *
     * @return An `Expected` instance containing the printed value.
     */
    def toExpected: Expected = Expected(value.value.printed)
}
