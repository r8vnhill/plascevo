/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package assertions.exceptions

import munit.{FailExceptionLike, Location}

/**
 * An exception that represents a failure due to a mismatch between expected and actual values during a comparison.
 *
 * The `ComparisonFailException` class extends `AssertionError` and is typically thrown when a test assertion fails
 * because the expected value does not match the actual value. This exception provides additional context by storing the
 * expected and actual values, and it can also include a custom error message and cause.
 *
 * @param message               An optional error message describing the failure. If the message is `null` or empty,
 *                              a default message is generated using the expected and actual values.
 * @param expected              The expected value in the comparison. Stored as an `Option[Any]` to handle `null` values.
 * @param actual                The actual value found in the comparison. Stored as an `Option[Any]` to handle `null`
 *                              values.
 * @param _isStackTracesEnabled A flag indicating whether stack traces should be enabled for this exception.
 * @param cause                 The underlying cause of this exception. If provided, it will be attached to this
 *                              exception.
 * @throws IllegalArgumentException If the `cause` is equal to `ComparisonFailException.NoCause`.
 */
final class ComparisonFailException private(
    message: Option[String],
    val expected: Option[Any],
    val actual: Option[Any],
    _isStackTracesEnabled: Boolean,
    val location: Location,
    cause: Option[Throwable]
) extends AssertionError(
    message match {
        case Some(msg) if msg.trim.nonEmpty => msg
        case _ => s"Expected: ${expected.getOrElse("<null>")}, found: ${actual.getOrElse("<null>")}"
    }
) with FailExceptionLike[ComparisonFailException] {

    // Initializes the cause of the exception if it's provided and not the NoCause marker
    if (cause != ComparisonFailException.NoCause) {
        initCause(cause.orNull)
    }

    /**
     * Secondary constructor that allows creating an exception with non-optional parameters.
     *
     * @param message  A descriptive message for the failure.
     * @param expected The expected value in the comparison.
     * @param actual   The actual value found in the comparison.
     * @param cause    The underlying cause of the exception.
     */
    def this(message: String, expected: Any, actual: Any, location: Location, cause: Option[Throwable]) = {
        this(Option(message), Option(expected), Option(actual), _isStackTracesEnabled = true, location, cause)
    }

    /**
     * Overrides the default stack trace filling mechanism.
     *
     * If stack traces are disabled, the stack trace is truncated to only the first element.
     *
     * @return The exception instance with the potentially modified stack trace.
     */
    override def fillInStackTrace(): Throwable = {
        val result = super.fillInStackTrace()
        if (!_isStackTracesEnabled) {
            result.setStackTrace(result.getStackTrace.slice(0, 1))
        }
        result
    }

    /**
     * Creates a new `ComparisonFailException` with a specified custom message.
     * 
     * @param message The custom error message to associate with the new `ComparisonFailException`.
     * @return A new `ComparisonFailException` instance with the specified message and the same properties as the
     *         original exception.
     */
    override def withMessage(
        message: String
    ): ComparisonFailException = new ComparisonFailException(
        Some(message),
        expected,
        actual,
        _isStackTracesEnabled,
        location,
        cause
    )

    /**
     * Indicates whether stack traces are enabled for this `ComparisonFailException`.
     */
    override def isStackTracesEnabled: Boolean = _isStackTracesEnabled
}

/**
 * Companion object for the `ComparisonFailException` class.
 *
 * The `ComparisonFailException` companion object provides utility members for the `ComparisonFailException` class. It
 * includes a special marker `NoCause`, which is used internally to indicate the absence of a cause in a way that avoids
 * the overhead of generating a stack trace.
 */
private object ComparisonFailException {

    /**
     * A marker exception used to indicate the absence of a cause.
     *
     * The `NoCause` exception is a special `RuntimeException` that is used internally within `ComparisonFailException`
     * to represent cases where no underlying cause is provided. To optimize performance, the `fillInStackTrace` method
     * is overridden to avoid filling in the stack trace, as this exception is only used as a marker and not intended
     * to be thrown or logged with a full stack trace.
     */
    private val NoCause = new RuntimeException("No cause") {
        override def fillInStackTrace(): Throwable = this // Avoid the overhead of filling in the stack trace for this
    }
}

