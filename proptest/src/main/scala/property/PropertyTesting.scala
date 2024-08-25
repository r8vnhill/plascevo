/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property

import property.SysPropEnv.sysprop

import cl.ravenhill.plascevo.property.statistics.StatisticsReporterMode.On
import cl.ravenhill.plascevo.property.statistics.{DefaultStatisticsReporter, StatisticsReporter, StatisticsReporterMode}

/**
 * An object containing default configuration settings and properties for property-based testing in Plascevo.
 *
 * The `PropertyTesting` object provides a set of default values and settings used during property-based testing. These
 * settings include default seeds, success and failure thresholds, shrinking modes, listeners, and various other
 * properties that can be configured through system properties or programmatically.
 *
 * @note The configuration values in this object can be modified either through system properties (when running the
 *       application) or by directly setting the variables in the code. These settings control the behavior of
 *       property-based testing in the Plascevo framework.
 */
object PropertyTesting {

    /** The default seed used for random number generation in property-based tests, if specified. */
    var defaultSeed: Option[Long] = sysprop("plascevo.proptest.default.seed", None) { s => Some(s.toLong) }

    /** The minimum number of successful tests required to consider a property-based test successful. */
    var defaultMinSuccess: Int = sysprop("plascevo.proptest.default.min-success", Int.MaxValue) {
        _.toInt
    }

    /** The maximum number of allowed failures in a property-based test. */
    var defaultMaxFailure: Int = sysprop("plascevo.proptest.default.max-failure", 0) {
        _.toInt
    }

    /** The default mode of shrinking used in property-based tests. */
    var defaultShrinkingMode: ShrinkingMode = BoundedShrinking(1000)

    /** The default set of listeners that can be used to monitor property-based tests. */
    var defaultListeners: Seq[PropertyTestListener] = Seq.empty

    /** The probability of generating edge cases during property-based tests. */
    var defaultEdgeCasesGenerationProbability: Double =
        sysprop("plascevo.proptest.arb.edgecases-generation-probability", 0.02) {
            _.toDouble
        }

    /** Whether to output classifications during property-based tests. */
    var defaultOutputClassifications: Boolean = sysprop("plascevo.proptest.arb.output.classifications", false) {
        _.toBoolean
    }

    /** Whether to fail the test if a seed is provided, ensuring reproducibility. */
    var failOnSeed: Boolean = sysprop("plascevo.proptest.seed.fail-if-set", false) {
        _.toBoolean
    }

    /** The default number of iterations to perform in a property-based test. */
    var defaultIterations: Int = sysprop("plascevo.proptest.default.iteration.count", 1000) {
        _.toInt
    }

    /** The default statistics reporter used to output test results. */
    var statisticsReporter: StatisticsReporter = DefaultStatisticsReporter

    /** The mode of the statistics reporter. */
    var statisticsReporterMode: StatisticsReporterMode = On
}
