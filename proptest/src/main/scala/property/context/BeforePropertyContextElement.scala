/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.context

import property.listeners.BeforeProperty

/**
 * A context element for handling actions to be executed before a property-based test.
 *
 * The `BeforePropertyContextElement` case class encapsulates the logic that needs to be executed before a 
 * property-based test begins. This class is typically used within a property testing framework to ensure that any 
 * necessary setup or pre-test operations are performed consistently.
 *
 * @param before An instance of the `BeforeProperty` trait that defines the action to be executed before the test.
 */
final case class BeforePropertyContextElement(before: BeforeProperty) {

    /**
     * Executes the action defined in the `BeforeProperty` instance.
     *
     * The `runBefore` method invokes the `apply` method of the `BeforeProperty` instance, triggering any logic that 
     * needs to be performed before the property-based test begins. This is typically used for setting up the test 
     * environment or initializing resources.
     */
    def runBefore(): Unit = before.apply()
}
