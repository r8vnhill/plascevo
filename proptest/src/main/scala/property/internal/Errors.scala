/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.internal

import assertions.print.Printed
import matchers.ApplyMatcher.{failure, failureWithCause}
import property.arbitrary.shrinkers.ShrinkResult

/**
 * Utility object for handling errors in property-based testing.
 *
 * The `Errors` object provides a set of methods for creating and throwing exceptions related to the failure of
 * property-based tests. It includes functionality for generating detailed failure messages that include information
 * about test attempts, shrinking results, seeds for reproducibility, and the causes of failures. These utilities are
 * intended to standardize error handling and reporting within the property-based testing framework.
 */
private[property] object Errors {
    
    /**
     * Throws a `Throwable` representing a failure in a property-based test.
     *
     * The `throwPropertyTestFailException` method constructs a `Throwable` using the provided exception, the number of 
     * test attempts, and the random seed used during the test. The exception is then immediately thrown. This method 
     * serves as a utility to standardize the throwing of exceptions in the context of property-based testing.
     *
     * @param e The original exception that caused the test to fail.
     * @param attempts The number of attempts made before the test failed.
     * @param seed The seed used for the random number generator, which allows the test to be reproduced.
     * @throws Throwable The constructed exception, which includes a detailed failure message and the original
     *                   exception.
     */
    def throwPropertyTestFailException(
        e: Throwable, attempts: Int, seed: Long
    ): Nothing = throw propertyFailException(e, attempts, seed, Seq.empty)

    /**
     * Creates a `Throwable` that encapsulates a detailed failure message for a property-based test.
     *
     * The `propertyFailException` method generates a `Throwable` object that contains a failure message constructed
     * from the provided exception, the number of test attempts, the random seed used for the test, and the results of
     * any shrinking that occurred. This exception can be thrown or logged to capture the context of a property-based
     * test failure.
     *
     * @param e The original exception that caused the test to fail.
     * @param attempts The number of attempts made before the test failed.
     * @param seed The seed used for the random number generator, which allows the test to be reproduced.
     * @param results A sequence of `ShrinkResult` instances representing the initial and shrunk values during the 
     *                shrinking process.
     * @return A `Throwable` containing a detailed failure message, including the number of attempts, shrinking results, 
     *         seed information, and the original exception.
     */
    private def propertyFailException(
        e: Throwable, attempts: Int, seed: Long, results: Seq[ShrinkResult[?]]
    ): Throwable = {
        failureWithCause(propertyTestFailMessage(attempts, results, seed, e), Some(e))
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
     * @param results A sequence of `ShrinkResult` instances, representing the initial and shrunk values during the 
     *                shrinking process.
     * @param seed The seed used for the random number generator, allowing the test to be reproduced.
     * @param e The exception that caused the test to fail.
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
