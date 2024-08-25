/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.context

/**
 * A trait for managing pre- and post-test actions in property-based testing.
 *
 * The `PropertyContextElement` trait provides a standardized interface for defining elements that manage actions 
 * executed before and after a property-based test. Implementations of this trait can specify optional actions that
 * should be performed at specific points in the test lifecycle, ensuring that necessary setup and teardown operations
 * are correctly handled.
 */
trait PropertyContextElement {

    /**
     * An optional element defining the action to be performed before a property-based test.
     *
     * This method returns an `Option` containing a `BeforePropertyContextElement` instance, which encapsulates the 
     * logic to be executed before the test starts. If no such action is defined, the method returns `None`.
     *
     * @return An optional `BeforePropertyContextElement` representing the action to be performed before the test.
     */
    def beforePropertyContextElement: Option[BeforePropertyContextElement] = None

    /**
     * An optional element defining the action to be performed after a property-based test.
     *
     * This method returns an `Option` containing an `AfterPropertyContextElement` instance, which encapsulates the 
     * logic to be executed after the test completes. If no such action is defined, the method returns `None`.
     *
     * @return An optional `AfterPropertyContextElement` representing the action to be performed after the test.
     */
    def afterPropertyContextElement: Option[AfterPropertyContextElement] = None
}
