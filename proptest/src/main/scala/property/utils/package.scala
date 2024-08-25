/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property

import cl.ravenhill.composerr.Constrained.constrainedTo
import cl.ravenhill.composerr.constraints.iterable.BeEmpty

package object utils {
    extension [T](it: Iterable[T]) {

        /**
         * Selects a random element from the `Iterable`.
         *
         * This method applies a constraint to ensure the `Iterable` is not empty, and then selects a random element
         * from it.
         *
         * @param random An instance of `scala.util.Random` used to generate the random index.
         * @return A random element from the `Iterable`.
         * @throws CompositeException          containing all infringed constraints.
         * @throws IterableConstraintException if the iterable is empty. 
         */
        def random(random: scala.util.Random): T = {
            it.constrainedTo {
                "Iterable must not be empty" | {
                    it mustNot BeEmpty[T]()
                }
            }.toSeq(random.nextInt(it.size))
        }
    }
}
