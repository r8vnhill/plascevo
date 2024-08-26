/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.checkall

import arbitrary.EdgeConfiguration
import classifications.{LabelsReporter, StandardLabelsReporter}

import cl.ravenhill.composerr.Constrained.constrained
import cl.ravenhill.composerr.constraints.option.BeNone
import cl.ravenhill.munit.collectors.ErrorCollector
import munit.checkall.stacktraces.PropertyCheckStackTraces

/**
 * Configuration settings for property-based testing.
 *
 * The `PropTestConfig` case class encapsulates various settings used in property-based testing, allowing customization
 * of how tests are executed, including seeding, success criteria, failure limits, and more.
 *
 * @param seed                  An optional seed value for the random number generator used in the tests. This allows for reproducible
 *                              test runs. If not provided, the default seed from `PropertyTesting` is used.
 * @param minSuccess            The minimum number of successful test cases required for the test to pass. This determines how many
 *                              test cases must pass before the property is considered validated. Defaults to
 *                              [[PropertyTesting.defaultMinSuccess]].
 * @param maxFailure            The maximum number of allowed failures before the test is halted. This sets a limit on how many
 *                              times a test can fail before it is considered to have failed overall. Defaults to
 *                              [[PropertyTesting.defaultMaxFailure]].
 * @param shrinkingMode         The mode used for shrinking failing test cases to simpler versions. Shrinking is a process used
 *                              in property testing to simplify failing test cases to their minimal failing input. Defaults to
 *                              [[PropertyTesting.defaultShrinkingMode]].
 * @param iterations            An optional number of iterations for the test. If provided, this will override the default number
 *                              of iterations for running the property tests.
 * @param listeners             A list of listeners that observe and respond to events during property testing. These listeners can
 *                              be used to customize the reporting or handling of test results. Defaults to
 *                              [[PropertyTesting.defaultListeners]].
 * @param edgeConfig            Configuration settings for handling edge cases during testing. Edge cases are extreme or boundary
 *                              values that are often a source of bugs. Defaults to the edge configuration provided by
 *                              `EdgeConfig.default()`.
 * @param outputClassifications A boolean flag indicating whether to output classifications of test cases. This is
 *                              useful for categorizing and analyzing the distribution of test cases. Defaults to
 *                              [[PropertyTesting.defaultOutputClassifications]].
 * @param labelsReporter        The reporter responsible for handling the output of test classifications and labels. This
 *                              controls how classification data is presented. Defaults to `StandardLabelsReporter`.
 * @param constraints           An optional set of constraints that can be applied to the generated test cases. These constraints
 *                              can limit or modify the range of generated values, ensuring they fit specific criteria.
 * @param maxDiscardPercentage  The maximum percentage of discarded test cases allowed before the test is considered
 *                              invalid. Discarded test cases are those that do not satisfy the preconditions of the
 *                              property. The default value is 20%.
 */
case class PropTestConfig(
    seed: Option[Long] = PropertyTesting.defaultSeed,
    minSuccess: Int = PropertyTesting.defaultMinSuccess,
    maxFailure: Int = PropertyTesting.defaultMaxFailure,
    shrinkingMode: ShrinkingMode = PropertyTesting.defaultShrinkingMode,
    iterations: Option[Int] = None,
    listeners: Seq[PropertyTestListener] = PropertyTesting.defaultListeners,
    edgeConfig: EdgeConfiguration = EdgeConfiguration.default,
    outputClassifications: Boolean = PropertyTesting.defaultOutputClassifications,
    labelsReporter: LabelsReporter = StandardLabelsReporter,
    constraints: Option[PropertyConstraints] = None,
    maxDiscardPercentage: Int = 20,
    errorCollector: ErrorCollector = ErrorCollector.default,
    stackTraces: PropertyCheckStackTraces = PropertyTesting.defaultStackTraces
) {
    /**
     * Validates the presence of a seed if the `failOnSeed` configuration is enabled.
     *
     * The `checkFailOnSeed` method ensures that a seed is provided when the `PropertyTesting.failOnSeed` configuration
     * is enabled. This is critical for reproducibility in property-based testing, as the seed allows tests to be rerun
     * with the same initial conditions. If the `failOnSeed` flag is set to `true`, this method will enforce that a seed
     * is present; otherwise, it will raise a constraint violation.
     *
     * @throws CompositeException        Containing all constraints that failed.
     * @throws OptionConstraintException If the seed is not provided and `PropertyTesting.failOnSeed` is enabled;
     *                                   wrapped in a `CompositeException`.
     */
    private[checkall] def checkFailOnSeed(): Unit = {
        constrained {
            if (PropertyTesting.failOnSeed) {
                "Seed must be provided for reproducibility" | {
                    seed mustNot BeNone
                }
            }
        }
    }
}
