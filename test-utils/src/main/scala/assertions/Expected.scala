/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package assertions

import assertions.print.Printed

/**
 * Represents the expected value in a comparison operation.
 *
 * The `Expected` case class is used to encapsulate the expected value during a comparison. It is typically used in
 * scenarios where an assertion or validation is being performed, and there is a need to clearly differentiate between
 * the expected and actual values. The value is wrapped in a `Printed` instance, which may provide a formatted or
 * display-friendly representation of the expected value.
 *
 * @param value The expected value, wrapped in a `Printed` instance.
 */
case class Expected(value: Printed)
