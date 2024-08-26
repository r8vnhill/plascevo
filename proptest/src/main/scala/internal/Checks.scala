/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.checkall
package internal

import context.PropertyContext

import cl.ravenhill.munit.collectors.ErrorCollector
import munit.FailException
import munit.checkall.internal.Exceptions.createPropertyFailException

import scala.util.Try

private[checkall] object Checks {

    /**
     * Checks that the property test has met the minimum required number of successful executions.
     *
     * The `checkMinSuccessful` method ensures that the number of successful test executions meets or exceeds the 
     * `minSuccess` threshold defined in the `PropTestConfig`. If the number of successes is less than the threshold, 
     * it throws a failure exception. This method is wrapped in a `Try` to handle any potential exceptions during the 
     * execution of the check.
     *
     * @param seed The seed used for the random number generator, allowing the test to be reproduced.
     * @param errorCollector An implicit `ErrorCollector` used to collect errors during the execution.
     * @param configuration An implicit `PropTestConfig` containing the configuration for the property test, including
     *                      the `minSuccess` threshold.
     * @param context An implicit `PropertyContext` that tracks the state of the property test, including the number of 
     *                successes and attempts.
     * @return A `Try[Unit]` indicating the success or failure of the check. If the check fails, an exception is thrown 
     *         with a detailed error message.
     */
    def checkMinSuccessful(seed: Long)(using errorCollector: ErrorCollector)
        (using configuration: PropTestConfig, context: PropertyContext): Try[Unit] = Try {
        val min = math.min(configuration.minSuccess, context.attempts)
        if (context.successes < min) {
            val error = s"Property passed ${context.successes} times (minSuccess rate was $min)\n"
            createPropertyFailException(PropertyFailException(error), context.attempts, seed)
        }
    }

    def checkMaxDiscarded(): Boolean = ???
}
