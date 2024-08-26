/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.internal

import property.*
import property.arbitrary.generators.{Arbitrary, Generator}
import property.arbitrary.shrinkers.Shrinker.shrinkFnFor
import property.context.PropertyContext
import property.seed.createRandom

object PropertyTest {

    def apply[A](name: String, gen: Generator[A])(property: A => Unit)(using config: PropTestConfig)
        (using context: PropertyContext): PropertyContext = {
        config.checkFailOnSeed()

        val constraints: PropertyConstraints = config.constraints
            .getOrElse(
                config.iterations
                    .map(PropertyConstraints.iterations)
                    .getOrElse(PropertyConstraints.iterations(PropertyTesting.defaultIterations))
            )

        val context = new PropertyContext(name)
        val random = createRandom
        val contextRandom = RandomSource.seeded(random.seed)

        gen match
            case arb: Arbitrary[?] => arb.generate(random, config.edgeConfig)
                .takeWhile(_ => constraints(context))
                .foreach { value =>
                    val contextualSeed = contextRandom.random.nextLong()
                    val shrinkFn = shrinkFnFor(value, property, config.shrinkingMode, contextualSeed)
                    config.listeners.foreach(_.beforeTest())
                    Test(
                        config,
                        shrinkFn,
                        Seq(value.value),
                        Seq(gen.classifier),
                        random.seed,
                        contextualSeed
                    ) {
                        property(value.value)
                    }.get
                    config.listeners.foreach(_.afterTest())
                }
            case _ => ???

        context.onSuccess(1, random)
        context
    }

    def apply[A, B](genA: Generator[A], genB: Generator[B])
        (f: (A, B) => Unit)
        (using config: PropTestConfig): PropertyContext = ???
}
