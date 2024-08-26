/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property

import property.arbitrary.generators.Generator
import property.context.PropertyContext
import property.internal.PropertyTest

import munit.{FunSuite, Location}

/**
 * A trait that extends `FunSuite` to provide utility methods for property-based testing. The `CheckAllSuite` trait
 * offers methods to easily create and run property tests with one or more generators.
 */
trait CheckAllSuite extends FunSuite {
    private var _name: Option[String] = None

    override def test(name: String)(body: => Any)(using loc: Location): Unit = {
        _name = Some(name)
        super.test(name)(body)
    }

    /**
     * Runs a property-based test with a single generator.
     *
     * @param gen      The generator that produces values of type `A` to test.
     * @param f        A function that tests the generated values of type `A`.
     * @param config   (implicit) The configuration for the property test.
     * @param context  (implicit) The context for the property test, defaulting to a new `PropertyContext` with the given
     *                 `name`.
     * @param location (implicit) The source code location where the test is defined, used for better error reporting.
     * @tparam A The type of values generated by the `gen` and passed to the test function `f`.
     * @return The `PropertyContext` after the test is executed.
     */
    def checkAll[A](gen: Generator[A])(f: A => Unit)
        (
            using config: PropTestConfig, 
            context: PropertyContext = PropertyContext(_name.getOrElse("Anonymous")),
            location: Location
        ): PropertyContext = PropertyTest(_name.getOrElse("Anonymous"), gen)(f)

    /**
     * Runs a property-based test with two generators.
     *
     * @param genA   The first generator that produces values of type `A`.
     * @param genB   The second generator that produces values of type `B`.
     * @param f      A function that tests the generated values of types `A` and `B`.
     * @param config (implicit) The configuration for the property test.
     * @tparam A The type of values generated by `genA` and passed to the test function `f`.
     * @tparam B The type of values generated by `genB` and passed to the test function `f`.
     * @return The `PropertyContext` after the test is executed.
     */
    def checkAll[A, B](genA: Generator[A], genB: Generator[B])(f: (A, B) => Unit)
        (using config: PropTestConfig): PropertyContext =
        PropertyTest(genA, genB)(f)
}
