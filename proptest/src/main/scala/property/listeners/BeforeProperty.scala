/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.listeners

/**
 * A trait representing an action to be executed before a property test.
 *
 * The `BeforeProperty` trait defines a single method, `apply`, which is intended to be implemented by classes or
 * objects that need to perform a specific action before a property-based test is executed. This can be useful in
 * scenarios where certain setup or initialization steps are required before running the actual test logic.
 *
 * Example use cases include:
 * - Initializing resources such as databases, files, or network connections.
 * - Setting up mock objects or test doubles.
 * - Logging or printing diagnostic information before the test.
 */
trait BeforeProperty {
    def apply(): Unit
}
