/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package assertions

import assertions.print.Printed

/**
 * Represents the actual value in a comparison operation.
 *
 * The `Actual` case class is used to encapsulate the actual value during a comparison. It is typically used in
 * scenarios where an assertion or validation is being performed, and there is a need to clearly differentiate between
 * the expected and actual values. The value is wrapped in a `Printed` instance, which provides a formatted or
 * display-friendly representation of the actual value.
 *
 * @param value The actual value, wrapped in a `Printed` instance.
 */
case class Actual(value: Printed)
