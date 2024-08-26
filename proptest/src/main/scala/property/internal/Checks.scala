/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.internal

import property.PropTestConfig
import property.context.PropertyContext
import property.internal.Errors.throwPropertyTestFailException

private[property] object Checks {
    
    /**
     * Checks whether the minimum number of successful test attempts has been reached.
     *
     * The `checkMinSuccessful` method ensures that the number of successful attempts in a property-based test meets or
     * exceeds the configured minimum success rate. If the number of successes is below the required minimum, an
     * exception is thrown, indicating that the property test has failed.
     *
     * @param seed The seed used for the random number generator, which allows the test to be reproduced.
     * @param configuration The implicit `PropTestConfig` providing the configuration for the property test, including
     *                      the minimum number of successful attempts required.
     * @param context The implicit `PropertyContext` representing the current state of the property test, including the
     *                number of attempts and successes.
     *
     * @throws AssertionError if the number of successful attempts is less than the minimum required by the
     *                        configuration. This exception will include details about the number of successes and the
     *                        minimum success rate required.
     */
    def checkMinSuccessful(seed: Long)
        (using configuration: PropTestConfig, context: PropertyContext): Unit = {
        val min = math.min(configuration.minSuccess, context.attempts)
        if (context.successes < min) {
            val error = s"Property passed ${context.successes} times (minSuccess rate was $min)\n"
            throwPropertyTestFailException(AssertionError(error), context.attempts, seed)
        }
    }

    def checkMaxDiscarded(): Boolean = ???
}
