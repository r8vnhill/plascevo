/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.classifications

import property.PropertyResult

/**
 * A trait for reporting the results of property-based tests, specifically focusing on labels and classifications.
 *
 * The `LabelsReporter` trait defines a contract for outputting the results of property-based tests that involve
 * classifications or labels. Implementations of this trait are responsible for taking a `PropertyResult` object and
 * outputting the relevant information, which may include classified outcomes, success rates, and other metrics tracked
 * during the test.
 *
 * This trait is intended to be extended or mixed into classes that handle the reporting of test results, allowing for
 * flexibility in how the results are presented, whether through console output, logging, file writing, or other means.
 */
trait LabelsReporter {

    /**
     * Outputs the results of a property-based test.
     *
     * The `output` method takes a `PropertyResult` object, which contains the results of a property-based test, 
     * including classifications and statistics such as the number of successes, failures, and attempts. 
     * Implementations of this method are expected to process and display this information in a meaningful way.
     *
     * @param result The `PropertyResult` object that contains the test results to be reported. This includes the inputs
     *               used, the seed, the number of attempts, successes, failures, and the labels associated with the
     *               test cases.
     */
    def output(result: PropertyResult): Unit
}
