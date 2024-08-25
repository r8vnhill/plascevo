/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.internal

import property.*
import property.seed.createRandom
import cl.ravenhill.plascevo.property.arbitrary.generators.{Arbitrary, Generator}

object PropertyTest {

    def apply[A](gen: Generator[A])(f: A => Unit)
        (using config: PropTestConfig): PropertyContext = {
        config.checkFailOnSeed()

        val constraints = config.constraints
            .getOrElse(
                config.iterations
                    .map(PropertyConstraints.iterations)
                    .getOrElse(PropertyConstraints.iterations(PropertyTesting.defaultIterations))
            )

        val context = new PropertyContext
        val random = createRandom
        val contextRandom = RandomSource.seeded(random.seed)

        gen match
            case _: Arbitrary[?] => ???
            case _ => ???

        context.onSuccess(1, random)
        context
    }

    def apply[A, B](genA: Generator[A], genB: Generator[B])
        (f: (A, B) => Unit)
        (using config: PropTestConfig): PropertyContext = ???
}
