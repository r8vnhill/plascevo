/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.checkall
package internal

import arbitrary.generators.{Arbitrary, Generator}
import arbitrary.shrinkers.Shrinker.shrinkFnFor
import context.PropertyContext
import seed.createRandom

import cl.ravenhill.munit.collectors.ErrorCollector
import munit.checkall.stacktraces.PropertyCheckStackTraces

import scala.util.{Failure, Success, Try}

object PropertyTest {

    def apply[A](name: String, gen: Generator[A])(property: A => Unit)(using config: PropTestConfig)
        (using context: PropertyContext): Try[PropertyContext] = {
        config.checkFailOnSeed()

        given ErrorCollector = config.errorCollector
        given PropertyCheckStackTraces = config.stackTraces
        
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

        context.onSuccess(1, random) match
            case Success(_) => Success(context)
            case Failure(e) => Failure(e)
    }

    def apply[A, B](genA: Generator[A], genB: Generator[B])
        (f: (A, B) => Unit)
        (using config: PropTestConfig): PropertyContext = ???
}
