/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.munit

import collectors.ErrorCollector
import collectors.ErrorCollector.clueContextAsString

import munit.{ComparisonFailException, Location}

import scala.util.Try

/**
 * Utility object for creating and handling exceptions in a robust and flexible manner.
 *
 * The `Exceptions` object provides methods to safely create exceptions, particularly focusing on situations where 
 * accessing the stack trace or other error-related details might itself cause issues. These methods return `Try`
 * instances, encapsulating potential failures during the creation of exceptions.
 */
object Exceptions {

    /**
     * Creates a `ComparisonFailException` with optional stack trace handling, based on the provided parameters.
     *
     * The `createComparisonFailException` method attempts to create a `ComparisonFailException` with a stack trace
     * derived from the provided cause. If accessing the stack trace causes an exception, a `ComparisonFailException`
     * is created without the stack trace. This method is used in scenarios where comparison failures need to be 
     * reported, and the presence of a stack trace is optional based on whether it can be safely accessed.
     *
     * @param message  A descriptive message indicating the nature of the comparison failure.
     * @param actual   The actual value that was obtained during the comparison.
     * @param expected The expected value that the actual value was compared against.
     * @param cause    An optional `Throwable` representing the underlying cause of the failure.
     * @param location The `Location` in the code where the comparison failure occurred, providing context.
     * @return A `ComparisonFailException` containing the failure details, including the actual and expected values,
     *         and the cause if available. The exception will either include or exclude the stack trace based on
     *         whether it could be safely accessed.
     * @throws Exception if the stack trace cannot be accessed or another unexpected issue arises. The method will 
     *                   return a `ComparisonFailException` even if an exception occurs during processing.
     */
    def createComparisonFailException(
        message: String,
        actual: Option[Any],
        expected: Option[Any],
        cause: Option[Throwable]
    )(using location: Location): ComparisonFailException = Try {
        // Attempt to access the stack trace to prevent exceptions later in the ComparisonFailException constructor
        cause.map(_.getStackTrace)
        new ComparisonFailException(message, expected, actual, location, true)
    }.recover {
        // If accessing the stack trace causes an exception, create the ComparisonFailException without it
        case _: Throwable => new ComparisonFailException(message, expected, actual, location, false)
    }.get // Safe to call get since the recover block always returns a ComparisonFailException

    /**
     * Creates a `ComparisonFailException` with detailed information and an optional cause, while including contextual
     * clues from the current location.
     *
     * The `comparisonFailWithCause` method generates a `ComparisonFailException` by invoking the
     * `createComparisonFailException` method. It appends contextual clues to the provided message, then constructs the
     * exception with the specified actual and expected values, as well as an optional cause. This method is used in
     * scenarios where a comparison failure needs to be reported, with additional context provided by the `Location` and
     * `ErrorCollector`.
     *
     * @param message        The message describing the comparison failure, with additional contextual information.
     * @param actual         The actual value that was compared.
     * @param expected       The expected value that the actual value was compared against.
     * @param cause          An optional `Throwable` representing the underlying cause of the failure.
     * @param loc            The `Location` in the code where the comparison failure occurred, providing context.
     * @param errorCollector The `ErrorCollector` used to gather and report errors during the test.
     * @return A `ComparisonFailException` containing the failure details, including the actual and expected values,
     *         and the provided cause if available.
     */
    def comparisonFailWithCause(
        message: String,
        actual: Option[Any],
        expected: Option[Any],
        cause: Option[Throwable]
    )(using loc: Location, errorCollector: ErrorCollector): ComparisonFailException = createComparisonFailException(
        message = clueContextAsString + message,
        actual = actual,
        expected = expected,
        cause = cause
    )
}
