/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.listeners

/**
 * A trait representing an action to be executed after a property test.
 *
 * The `AfterProperty` trait defines a single method, `apply`, which is intended to be implemented by classes or objects
 * that need to perform a specific action after a property-based test has been executed. This is useful in scenarios
 * where cleanup or teardown steps are required once the test logic has been completed.
 *
 * Example use cases include:
 * - Releasing resources such as databases, files, or network connections.
 * - Resetting or cleaning up test doubles or mock objects.
 * - Logging or printing diagnostic information after the test.
 */
trait AfterProperty {
    def apply(): Unit
}
