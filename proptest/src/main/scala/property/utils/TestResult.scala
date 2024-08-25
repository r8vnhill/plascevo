/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.utils

/** Represents the outcome of a test, indicating whether it passed or failed. */
enum TestResult {
    /** Indicates that the test failed. */
    case Failure
    /** Indicates that the test passed. */
    case Success
}
