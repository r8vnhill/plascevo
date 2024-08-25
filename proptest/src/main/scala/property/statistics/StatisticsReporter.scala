/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.statistics

/**
 * Trait for reporting statistics during property-based testing.
 *
 * The `StatisticsReporter` trait defines the contract for classes that are responsible for outputting statistics 
 * generated during property-based testing. Implementations of this trait can customize how statistics are reported,
 * whether it's logging to a console, writing to a file, or any other form of output.
 */
trait StatisticsReporter {

    /**
     * Outputs the provided statistics.
     *
     * The `output` method is responsible for handling the output of `TestStatistics` data. The specific manner of
     * output (e.g., console, file, network) is determined by the implementation of this trait.
     *
     * @param statistics The `TestStatistics` object containing data to be reported. This includes details such as the
     *                   number of iterations, labels, success or failure status, and other relevant metrics.
     */
    def output(statistics: TestStatistics): Unit
}
