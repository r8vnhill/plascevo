/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.commons

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}

/**
 * An actor for handling and logging test name context elements.
 *
 * The `TestNameContextActor` object provides a behavior for an Akka actor that receives `TestNameContextElement`
 * messages. This actor logs the received test name and can be extended to handle additional context-related
 * functionality as needed.
 *
 * Usage Example:
 * {{{
 *   val system: ActorSystem[TestNameContextElement] = ActorSystem(TestNameContextActor(), "TestNameContextSystem")
 *   system ! TestNameContextElement("MyTestName")
 * }}}
 *
 * This actor is stateless by design, making it easily extensible for future needs, including handling multiple contexts
 * or more complex state management.
 */
object TestNameContextActor {

    /**
     * Creates the behavior for the `TestNameContextActor`.
     *
     * The `apply` method defines the setup for the actor and specifies its behavior. The actor logs the received test
     * name and remains in the same state (`Behaviors.same`) after handling each message.
     *
     * @return A behavior that handles `TestNameContextElement` messages and logs the received test name.
     */
    def apply(): Behavior[TestNameContextElement] = Behaviors.setup { (context: ActorContext[TestNameContextElement]) =>
        Behaviors.receiveMessage { message =>
            context.log.info(s"Received test name context: ${message.testName}")
            // Handle the context as needed
            Behaviors.same
        }
    }
}
