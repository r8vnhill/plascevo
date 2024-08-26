/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.commons

import akka.actor.typed.ActorSystem

import akka.actor.typed.ActorRef

/**
 * The TestNameContextElement class represents an element in a context that includes the name of a test.
 *
 * @param testName The name of the test associated with this context element.
 */
case class TestNameContextElement(testName: String)
