/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.checkall
package internal

import arbitrary.shrinkers.ShrinkResult

import cl.ravenhill.munit.Exceptions.failureWithCause
import cl.ravenhill.munit.collectors.ErrorCollector
import cl.ravenhill.munit.collectors.ErrorCollector.clueContextAsString
import cl.ravenhill.munit.print.Printed
import munit.{FailException, Location}

import scala.util.Try

/**
 * Utility object for handling errors in property-based testing.
 *
 * The `Errors` object provides a set of methods for creating and throwing exceptions related to the failure of
 * property-based tests. It includes functionality for generating detailed failure messages that include information
 * about test attempts, shrinking results, seeds for reproducibility, and the causes of failures. These utilities are
 * intended to standardize error handling and reporting within the property-based testing framework.
 */
private[checkall] object Exceptions {

    /**
     * Creates a `PropertyFailException` for a property-based test failure.
     *
     * The `createPropertyFailException` method generates a `PropertyFailException` that includes the provided throwable
     * `e`, the number of attempts made before the failure, and the seed used during the test. The exception is
     * encapsulated within a `Try`, allowing for controlled error handling and recovery in case the exception creation
     * fails.
     *
     * @param e              The original exception that caused the test to fail.
     * @param attempts       The number of attempts made before the test failed.
     * @param seed           The seed used for the random number generator, which allows the test to be reproduced.
     * @param errorCollector An implicit `ErrorCollector` used to collect errors during the execution.
     * @return A `Try[PropertyFailException]` containing the created exception, or an error if one occurred during the
     *         process.
     */
    def createPropertyFailException(
        e: Throwable,
        attempts: Int,
        seed: Long
    )(using errorCollector: ErrorCollector): Try[PropertyFailException] = {
        propertyFailException(e, attempts, seed, Seq.empty)
    }

    /**
     * Creates a `PropertyFailException` that includes details about the test failure and the results of shrinking.
     *
     * The `createPropertyFailExceptionWithResults` method generates a `PropertyFailException` that includes the
     * provided throwable `e`, the number of attempts made before the failure, the seed used during the test, and the
     * results of any shrinking operations. The exception is returned within a `Try`, allowing for controlled error handling and
     * recovery in case the exception creation fails.
     *
     * @param results        A sequence of `ShrinkResult` instances representing the initial and shrunk values during
     *                       the shrinking process.
     * @param e              The original exception that caused the test to fail.
     * @param attempts       The number of attempts made before the test failed.
     * @param seed           The seed used for the random number generator, which allows the test to be reproduced.
     * @param errorCollector An implicit `ErrorCollector` used to collect errors during the execution.
     * @return A `Try[PropertyFailException]` containing the created exception, or an error if one occurred during the
     *         process.
     */
    def createPropertyFailExceptionWithResults(
        results: Seq[ShrinkResult[?]],
        e: Throwable,
        attempts: Int,
        seed: Long
    )(using errorCollector: ErrorCollector): Try[PropertyFailException] = propertyFailException(e, attempts, seed, results)

    /**
     * Creates a `Throwable` that encapsulates a detailed failure message for a property-based test.
     *
     * The `propertyFailException` method generates a `Throwable` object that contains a failure message constructed
     * from the provided exception, the number of test attempts, the random seed used for the test, and the results of
     * any shrinking that occurred. This exception can be thrown or logged to capture the context of a property-based
     * test failure.
     *
     * @param cause    The original exception that caused the test to fail.
     * @param attempts The number of attempts made before the test failed.
     * @param seed     The seed used for the random number generator, which allows the test to be reproduced.
     * @param results  A sequence of `ShrinkResult` instances representing the initial and shrunk values during the
     *                 shrinking process.
     * @return A `Throwable` containing a detailed failure message, including the number of attempts, shrinking results,
     *         seed information, and the original exception.
     */
    private def propertyFailException(
        cause: Throwable,
        attempts: Int,
        seed: Long,
        results: Seq[ShrinkResult[?]]
    )(using errorCollector: ErrorCollector): Try[PropertyFailException] =
        propertyFailureWithCause(propertyTestFailMessage(attempts, results, seed, cause), Some(cause))

    /**
     * Creates a `PropertyFailException` with a given message and optional cause, handling any potential errors during
     * the creation process.
     *
     * @param message        The failure message describing the reason for the property-based test failure.
     * @param cause          An optional `Throwable` representing the underlying cause of the failure.
     * @param loc            An implicit `Location` parameter providing the location context of the failure.
     * @param errorCollector An implicit `ErrorCollector` used to collect errors during the execution.
     * @return A `Try[Throwable]` containing the `PropertyFailException` that encapsulates the failure details, or an
     *         error if one occurred during the creation of the exception.
     */
    private def propertyFailureWithCause(
        message: String,
        cause: Option[Throwable]
    )(using loc: Location, errorCollector: ErrorCollector): Try[PropertyFailException] = {
        createPropertyFailException(
            message = clueContextAsString + message,
            cause = cause
        )
    }

    /**
     * Attempts to create a `PropertyFailException` with the provided message and cause.
     *
     * @param message The failure message to be included in the exception.
     * @param cause   An optional `Throwable` representing the cause of the failure.
     * @return A `Try[PropertyFailException]` containing the created exception, or an error if one occurred during the
     *         process.
     */
    private def createPropertyFailException(
        message: String,
        cause: Option[Throwable]
    ): Try[PropertyFailException] = Try {
        // Attempt to access the stack trace to prevent exceptions later in the PropertyFailException constructor
        cause.map(_.getStackTrace)
        new PropertyFailException(message, cause)
    }.recover {
        // If accessing the stack trace causes an exception, create a PropertyFailException without a cause
        case _: Throwable => new PropertyFailException(message)
    }

    /**
     * Generates a detailed failure message for a property-based test.
     *
     * The `propertyTestFailMessage` method constructs a failure message when a property-based test fails. It includes 
     * information about the number of attempts, the results of shrinking, the seed used for reproducibility, and the 
     * cause of the failure. The generated message is useful for debugging and understanding the context of the test 
     * failure.
     *
     * @param attempts The number of attempts made before the test failed.
     * @param results  A sequence of `ShrinkResult` instances, representing the initial and shrunk values during the
     *                 shrinking process.
     * @param seed     The seed used for the random number generator, allowing the test to be reproduced.
     * @param e        The exception that caused the test to fail.
     * @return A `String` containing the detailed failure message, including attempts, shrinking results, seed
     *         information, and the cause of the failure.
     */
    private def propertyTestFailMessage(
        attempts: Int,
        results: Seq[ShrinkResult[?]],
        seed: Long,
        e: Throwable
    ): String = {
        val sb = new StringBuilder
        appendHeader(sb, attempts)
        appendResults(sb, results)
        appendSeedInfo(sb, seed)
        appendCause(sb, results, e)
        sb.toString()
    }

    /** Appends the header indicating the number of attempts to a `StringBuilder`.
     *
     * This method adds a header to the provided `StringBuilder`, specifying the number of attempts made before the
     * property test failed.
     *
     * @param sb       The `StringBuilder` to append the header to.
     * @param attempts The number of attempts made before the failure.
     */
    private def appendHeader(sb: StringBuilder, attempts: Int): Unit = {
        sb.append(s"Property failed after $attempts attempts\n")
    }

    /**
     * Appends the formatted results of shrinking operations to a `StringBuilder`.
     *
     * This method adds the results of shrinking operations to the provided `StringBuilder`. Each result is formatted
     * with its index and value, and displayed on a new line. If there are no results, nothing is appended.
     *
     * @param sb      The `StringBuilder` to append the results to.
     * @param results A sequence of shrinking results to be formatted and appended.
     */
    private def appendResults(sb: StringBuilder, results: Seq[ShrinkResult[?]]): Unit = {
        if (results.nonEmpty) {
            sb.append("\n")
            results.zipWithIndex.foreach { case (result, index) =>
                val input = formatResult(result, index)
                sb.append(input)
                sb.append("\n")
            }
        }
    }

    /** Formats the result of a shrinking operation for a specific argument.
     *
     * This method generates a string representation of a shrinking result, showing the argument index and its value.
     * If the value has been shrunk, the original value is also displayed.
     *
     * @param result The shrinking result to format.
     * @param index  The index of the argument being displayed.
     * @return A formatted string representing the shrinking result.
     */
    private def formatResult(result: ShrinkResult[?], index: Int): String = {
        if (result.initial == result.shrink) {
            s"\tArg $index: ${Printed(Some(result.initial))}"
        } else {
            s"\tArg $index: ${Printed(Some(result.initial))} (shrunk from ${result.initial})"
        }
    }

    /**
     * Appends the seed information to the provided StringBuilder, indicating how to repeat the test with the given
     * seed.
     */
    private def appendSeedInfo(sb: StringBuilder, seed: Long): Unit = {
        sb.append("\n")
        sb.append(s"Repeat this test by using seed $seed\n\n")
    }

    /**
     * Appends the formatted cause of failure to the provided StringBuilder, using the final determined cause from the
     * shrink results.
     */
    private def appendCause(sb: StringBuilder, results: Seq[ShrinkResult[?]], e: Throwable): Unit = {
        val finalCause = determineFinalCause(results, e)
        val causedBy = formatCause(finalCause)
        sb.append(causedBy)
    }

    /**
     * Determines the final cause of failure by iterating over the shrink results, using the original exception if no
     * specific cause is found.
     */
    private def determineFinalCause(results: Seq[ShrinkResult[?]], e: Throwable): Throwable = {
        results.foldLeft(e) { (t, result) =>
            result.cause.getOrElse(t)
        }
    }

    /** Formats the cause of the failure as a string. */
    private def formatCause(cause: Throwable): String = {
        cause match {
            case e: AssertionError => s"Caused by: ${Option(e.getMessage).map(_.trim).getOrElse("")}"
            case e => s"Caused by ${e.getClass.getSimpleName}: ${Option(e.getMessage).map(_.trim).getOrElse("")}"
        }
    }
}
