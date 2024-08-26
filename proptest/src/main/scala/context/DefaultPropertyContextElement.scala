/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.checkall
package context

/**
 * A default implementation of the `PropertyContextElement` trait, managing pre- and post-test actions.
 *
 * The `DefaultPropertyContextElement` class provides a standard implementation of the `PropertyContextElement`
 * trait. It encapsulates optional elements that represent actions to be executed before and after a property-based 
 * test. This class is useful for managing the lifecycle of property tests, ensuring that necessary setup and 
 * teardown operations are executed in the correct order.
 *
 * @param beforeElement An optional `BeforePropertyContextElement` that represents an action to be executed before the 
 *                      property test. If not provided, no action is taken before the test.
 * @param afterElement  An optional `AfterPropertyContextElement` that represents an action to be executed after the 
 *                      property test. If not provided, no action is taken after the test.
 */
class DefaultPropertyContextElement(
    val beforeElement: Option[BeforePropertyContextElement] = None,
    val afterElement: Option[AfterPropertyContextElement] = None
) extends PropertyContextElement
