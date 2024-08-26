/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.checkall
package statistics

import StatisticsReporterMode.{Failed, On, Success}
import munit.checkall.context.PropertyContext
import munit.checkall.utils.TestResult

/**
 * A utility object responsible for managing the output of test statistics during property-based testing.
 *
 * The `Output` object provides a method to output statistical information about a property-based test run. It 
 * determines when and how to output these statistics based on the success of the test and the configured reporting 
 * mode.
 */
object Output {

    /**
     * Outputs statistical information about the test run based on the test context and result.
     *
     * The `outputStatistics` method gathers relevant statistical data from the provided `PropertyContext` and test
     * result. Depending on the `statisticsReporterMode` configuration, it decides whether to output these statistics
     * through the `statisticsReporter`.
     *
     * @param context The `PropertyContext` representing the current state of the property-based test. It contains 
     *                information such as the number of attempts, labels, and statistics collected during the test run.
     * @param numArgs The number of arguments used in the property test. This is used to calculate the statistics 
     *                appropriately.
     * @param success The result of the test, which can be either `TestResult.Success` or `TestResult.Failure`. The 
     *                output behavior may change depending on this result.
     */
    def outputStatistics(context: PropertyContext, numArgs: Int, success: TestResult): Unit = {

        /** Writes the test statistics to the configured `statisticsReporter`. */
        def write(): Unit = {
            val statistics = TestStatistics(
                context.attempts,
                numArgs,
                context.labels,
                context.statistics,
                success
            )
            PropertyTesting.statisticsReporter.output(statistics)(using context)
        }

        PropertyTesting.statisticsReporterMode match {
            case On =>
                write()
            case Success =>
                success match {
                    case TestResult.Passed => write()
                    case _ => // Do nothing
                }
            case Failed =>
                success match {
                    case TestResult.Failure => write()
                    case _ => // Do nothing
                }
            case _ =>
            // Do nothing for other modes
        }
    }
}
