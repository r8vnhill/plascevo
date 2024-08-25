/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.context

import property.listeners.AfterProperty

/**
 * A context element for handling actions to be executed after a property-based test.
 *
 * The `AfterPropertyContextElement` case class encapsulates the logic to be executed after a property-based test
 * completes. This class is typically used within a property testing framework to ensure that any necessary teardown or
 * post-test operations are performed consistently.
 *
 * @param after An instance of the `AfterProperty` trait that defines the action to be executed after the test.
 */
case class AfterPropertyContextElement(after: AfterProperty) {

    /**
     * Executes the action defined in the `AfterProperty` instance.
     *
     * The `runAfter` method invokes the `apply` method of the `AfterProperty` instance, triggering any logic that needs
     * to be performed after the property-based test. This is typically used for cleanup or post-test assertions.
     */
    def runAfter(): Unit = after.apply()
}
