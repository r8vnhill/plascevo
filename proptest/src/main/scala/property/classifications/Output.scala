/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.classifications

import property.{PropTestConfig, PropertyResult}

import cl.ravenhill.plascevo.property.context.PropertyContext

/**
 * An object that provides functionality for outputting classifications in property-based testing.
 *
 * The `Output` object includes methods to generate and report results of property-based tests, particularly focusing on
 * the classification of test cases based on their outcomes.
 */
object Output {

    /**
     * Generates and outputs the classifications of test results based on the provided configuration and context.
     *
     * @param inputs  The number of inputs used in the property test, typically indicating the number of test cases.
     * @param config  The configuration for the property test, which controls aspects such as whether to output
     *                classifications.
     * @param seed    The seed value used for random number generation during the property test. This ensures
     *                reproducibility of test cases.
     * @param context (implicit) The context of the property test, which tracks attempts, successes, failures, and
     *                auto-classifications.
     */
    def outputClassifications(inputs: Int, config: PropTestConfig, seed: Long)
        (using context: PropertyContext): Unit = {
        // Create a PropertyResult instance with classifications and test results
        val result = PropertyResult(
            Seq.tabulate(inputs)(i => i.toString), // Generate a sequence of input identifiers as strings
            seed, // The seed used for generating inputs
            context.attempts, // The number of attempts made during the test
            context.successes, // The number of successful test cases
            context.failures, // The number of failed test cases
            context.autoClassifications // Any automatic classifications made during the test
        )

        // Output the result if the configuration allows for classification output
        if (config.outputClassifications) config.labelsReporter.output(result)
    }
}
