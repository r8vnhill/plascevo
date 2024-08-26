/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.checkall

import munit.{FailException, Location}

/**
 * Exception class representing a failure in a property-based test.
 *
 * The `PropertyFailException` is a specialized `FailException` used to indicate that a property-based test has failed.
 * It encapsulates a failure message and an optional cause. This exception can be used to provide detailed information
 * about the nature of the test failure, including the specific conditions or inputs that led to the failure.
 *
 * @param msg   The message describing the reason for the test failure.
 * @param cause An optional `Throwable` that caused the failure. If provided, it is passed to the `FailException`
 *              constructor; otherwise, the exception is created without a cause.
 */
class PropertyFailException(msg: String, cause: Option[Throwable])(using location: Location)
    extends FailException(msg, cause.orNull, location) {

    /**
     * Constructs a `PropertyFailException` with a message but without a cause.
     *
     * This constructor allows the creation of a `PropertyFailException` using only a failure message, without
     * specifying a cause. It delegates to the primary constructor, passing `None` as the cause.
     *
     * @param msg The message describing the reason for the test failure.
     */
    def this(msg: String) = this(msg, None)
}
