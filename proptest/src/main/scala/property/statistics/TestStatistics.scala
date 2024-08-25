/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.statistics

import property.utils.TestResult

/**
 * A case class representing the statistical data collected during a property-based test run.
 *
 * The `TestStatistics` case class encapsulates various metrics and details about a property-based test, including
 * the number of iterations, the arguments used, and the distribution of labels and classifications. This data is
 * used to analyze the behavior of the test and report results.
 *
 * @param iterations The total number of iterations or attempts made during the test run. This represents how many
 *                   times the property was evaluated with different inputs.
 * @param numArgs    The number of arguments used in each test iteration. This indicates the complexity or arity of
 *                   the property being tested.
 * @param labels     A set of labels associated with the test. Labels are typically used to categorize or classify
 *                   different outcomes or input scenarios within the test.
 * @param statistics A nested map representing the frequency of specific outcomes or classifications, categorized by
 *                   optional labels and associated values. The outer map key is an optional label, and the inner map
 *                   key is an optional classification value, with the corresponding count as the value.
 * @param success    The final result of the test, represented as a `TestResult`. This indicates whether the test
 *                   ultimately passed or failed.
 */
case class TestStatistics(
    iterations: Int,
    numArgs: Int,
    labels: Set[Label],
    statistics: Map[Option[Label], Map[Option[Any], Int]],
    success: TestResult
)
