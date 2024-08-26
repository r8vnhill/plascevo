/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.checkall

import context.PropertyContext

/**
 * Trait representing a set of constraints that can be applied during property-based testing.
 *
 * The `PropertyConstraints` trait defines a contract for evaluating whether certain conditions hold true within the
 * context of a property test. Implementations of this trait are responsible for defining specific constraints that
 * determine when a property test should continue or stop.
 */
trait PropertyConstraints {

    /**
     * Evaluates the constraint within the given property test context.
     *
     * The `apply` method is called to check whether the constraint is satisfied based on the current state of the
     * property test, as encapsulated by the `PropertyContext`. This method should return `true` if the test should
     * continue, or `false` if it should stop.
     *
     * @param context The `PropertyContext` providing information about the current state of the property test.
     * @return `true` if the constraint is satisfied and the test should continue, `false` otherwise.
     */
    def apply(context: PropertyContext): Boolean
}

/**
 * Companion object for the `PropertyConstraints` trait, providing predefined constraints.
 *
 * The `PropertyConstraints` object includes utility methods for creating common constraints used in property-based
 * testing. These constraints can be applied to control the execution of tests based on criteria such as the number of
 * iterations.
 */
object PropertyConstraints {

    /**
     * Creates a constraint that limits the number of iterations in a property test.
     *
     * The `iterations` method returns a constraint function that evaluates whether the number of evaluations
     * (iterations) performed in a property test is less than a specified limit. This constraint is useful for setting
     * an upper bound on the number of times a property test should be executed.
     *
     * @param k The maximum number of iterations allowed for the property test.
     * @return A function that evaluates whether the property test should continue based on the number of iterations.
     */
    def iterations(k: Int): PropertyConstraints = (context: PropertyContext) => context.evaluations < k
}
