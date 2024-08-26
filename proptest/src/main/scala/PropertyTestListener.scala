/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.checkall

/**
 * Trait for listening to events during property-based testing.
 *
 * The `PropertyTestListener` trait provides hooks that can be implemented to perform actions before and after each
 * property test. This is useful for setting up and tearing down resources, logging, or any other side effects that need
 * to occur in conjunction with property-based testing.
 *
 * Implementations of this trait can override the `beforeTest` and `afterTest` methods to define custom behavior that
 * should be executed before and after each test.
 */
trait PropertyTestListener {

    /**
     * Hook that is called before each property test.
     *
     * The `beforeTest` method is executed immediately before a property test is run. This method can be overridden by
     * implementations of `PropertyTestListener` to perform any setup actions or initialize resources required for the
     * test.
     *
     * By default, this method does nothing.
     */
    def beforeTest(): Unit = ()

    /**
     * Hook that is called after each property test.
     *
     * The `afterTest` method is executed immediately after a property test completes, regardless of whether the test
     * passes or fails. This method can be overridden by implementations of `PropertyTestListener` to perform any
     * cleanup actions or release resources used during the test.
     *
     * By default, this method does nothing.
     */
    def afterTest(): Unit = ()
}
