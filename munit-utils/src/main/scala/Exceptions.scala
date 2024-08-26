/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.munit

import collectors.ErrorCollector
import collectors.ErrorCollector.clueContextAsString

import munit.Location

import scala.util.{Success, Try}

/**
 * Utility object for creating and handling exceptions in a robust and flexible manner.
 *
 * The `Exceptions` object provides methods to safely create exceptions, particularly focusing on situations where 
 * accessing the stack trace or other error-related details might itself cause issues. These methods return `Try`
 * instances, encapsulating potential failures during the creation of exceptions.
 */
object Exceptions {

    /**
     * Creates an `AssertionError` with a specified message and an optional cause.
     *
     * The `createComparisonFailException` method attempts to create an `AssertionError` using the provided message and
     * an optional cause. It first tries to access the stack trace of the cause to prevent potential exceptions in the
     * `AssertionError` constructor. If accessing the stack trace itself causes an exception, an `AssertionError` is 
     * created without a cause.
     *
     * @param message The message describing the failure.
     * @param cause   An optional `Throwable` representing the underlying cause of the failure.
     * @return A `Try[AssertionError]` that contains either the created `AssertionError` or a fallback error without a 
     *         cause if an exception occurs.
     */
    def createComparisonFailException(
        message: String,
        cause: Option[Throwable]
    ): Try[AssertionError] = Try {
        // Attempt to access the stack trace to prevent exceptions later in the AssertionError constructor
        cause.map(_.getStackTrace)
        new AssertionError(message, cause.orNull)
    }.recover {
        // If accessing the stack trace causes an exception, create an AssertionError without a cause
        case _: Throwable => new AssertionError(message)
    }

    /**
     * Creates a `Throwable` representing a failure with a specified message and an optional cause, considering the 
     * current location context.
     *
     * The `failureWithCause` method generates a `Throwable` that encapsulates the provided failure message, optional
     * cause, and includes additional context information. It uses the `createComparisonFailException` method to handle 
     * the creation of the `Throwable`, ensuring that any exceptions are handled gracefully.
     *
     * @param message        A `String` representing the failure message to be included in the `Throwable`.
     * @param cause          An optional `Throwable` representing the underlying cause of the failure. If `Some(cause)`
     *                       is provided, it will be set as the cause of the `Throwable`. If `None`, the `Throwable`
     *                       will have no cause.
     * @param loc            An implicit `Location` parameter representing the location context where the failure
     *                       occurred.
     * @param errorCollector An implicit `ErrorCollector` parameter that collects error information during test runs.
     * @return A `Try[Throwable]` containing either the created `Throwable` or a fallback error if an exception occurs 
     *         during creation.
     */
    def failureWithCause(message: String, cause: Option[Throwable])
        (using loc: Location, errorCollector: ErrorCollector): Try[Throwable] =
        createComparisonFailException(
            message = clueContextAsString + message,
            cause = cause
        )
}
