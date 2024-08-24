/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package assertions

import munit.Location

package object exceptions {
    /**
     * Creates a failure exception with detailed comparison information.
     *
     * The `createComparisonFailException` method constructs a `ComparisonFailException` that provides detailed
     * information about a failed comparison, including the expected and actual values. The method attempts to access
     * the stack trace of the provided cause to avoid issues that can arise when working with mocked exceptions. If
     * accessing the stack trace causes an exception, the method catches it and creates the `ComparisonFailException`
     * without the cause.
     *
     * @param message  The message to include in the exception, describing the failure.
     * @param cause    An optional `Throwable` that represents the underlying cause of the failure.
     * @param expected The expected value in the comparison.
     * @param actual   The actual value in the comparison.
     * @param loc      An implicit `Location` providing information about where the failure occurred in the code.
     * @return A `Throwable` representing the failure, which is usually an instance of `ComparisonFailException`.
     */
    def createComparisonFailException(
        message: String,
        cause: Option[Throwable],
        expected: Expected,
        actual: Actual
    )(using loc: Location): Throwable =
        try {
            // In the case of a mock, attempting to access the cause's stack trace in the assertion error constructor
            // can lead to another exception being thrown. To avoid this, we try to access the stack trace ourselves
            // and catch any resulting exception.
            cause.map(_.getStackTrace)
            ComparisonFailException(message, expected, actual, loc, cause)
        } catch {
            // If an exception occurs while accessing the stack trace, create the ComparisonFailException without the
            // cause.
            case _: Throwable => ComparisonFailException(message, expected, actual, loc, None)
        }

    /**
     * Creates an `AssertionError` with an optional cause, handling potential issues with the cause's stack trace.
     *
     * The `createComparisonFailException` method attempts to create an `AssertionError` with a specified message and an 
     * optional cause. It includes a safeguard against potential errors that might occur when trying to access the 
     * stack trace of the cause. If accessing the stack trace throws an exception, the method falls back to creating 
     * an `AssertionError` without a cause.
     *
     * @param message The error message to be included in the `AssertionError`.
     * @param cause   An optional `Throwable` that caused this exception. If provided, it will be set as the cause of
     *                the `AssertionError`. If `None` is provided, the `AssertionError` will have no cause.
     * @return An `AssertionError` with the specified message and, if possible, the provided cause.
     * @throws AssertionError If an error occurs while processing the cause, an `AssertionError` without a cause is
     *                        created.
     */
    def createComparisonFailException(
        message: String,
        cause: Option[Throwable]
    ): AssertionError = try {
        // Attempt to access the stack trace to prevent exceptions later in the AssertionError constructor
        cause.map(_.getStackTrace)
        new AssertionError(message, cause.orNull)
    } catch {
        // If accessing the stack trace causes an exception, create an AssertionError without a cause
        case _: Throwable => new AssertionError(message)
    }
}
