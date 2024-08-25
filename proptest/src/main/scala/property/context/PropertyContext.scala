/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.context

import property.arbitrary.generators.Sample
import property.{PropTestConfig, RandomSource}

import cl.ravenhill.plascevo.property.classifications.Output.outputClassifications
import cl.ravenhill.plascevo.property.internal.Checks.{checkMaxDiscarded, checkMinSuccessful}
import cl.ravenhill.plascevo.property.seed.Seed.clearFailedSeeds
import cl.ravenhill.plascevo.property.statistics.Label
import cl.ravenhill.plascevo.property.statistics.Output.outputStatistics
import cl.ravenhill.plascevo.property.utils.TestResult.Success

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
 * Manages the state and lifecycle of a property-based test.
 *
 * The `PropertyContext` class provides the necessary context for executing property-based tests. It tracks the number
 * of evaluations performed during the test, manages the random source specific to the current test context, and handles
 * generated samples. This context is essential for coordinating the execution flow of property tests, including setting
 * up the random source and marking evaluations.
 *
 * @param configuration The configuration settings for the property-based testing process, provided implicitly.
 */
final class PropertyContext(using val configuration: PropTestConfig = PropTestConfig()) {

    /** Tracks the number of evaluations performed in the current test context.
     *
     * The `_evaluations` variable is incremented each time an evaluation is marked, keeping count of how many times
     * the property test has been evaluated.
     */
    private var _evaluations: Int = 0

    /** Stores the random source specific to the current test context.
     *
     * The `_contextualRandomSource` variable holds an optional `RandomSource` that is set up for the current test
     * context. This allows the test to use a consistent random source throughout its execution.
     */
    private var _contextualRandomSource: Option[RandomSource] = None

    /** Holds the samples generated during the test execution.
     *
     * The `_generatedSamples` sequence stores samples of type `Sample[?]` that are generated while the test is running.
     * This collection is cleared when a new contextual random source is set up.
     */
    private val _generatedSamples: ListBuffer[Sample[?]] = ListBuffer.empty

    /**
     * Holds an optional `BeforePropertyContextElement` that contains logic to be executed before the property-based
     * test. This private member is initialized to `None` and can be set to a specific `BeforePropertyContextElement`
     * as needed.
     */
    private var _beforePropertyContextElement: Option[BeforePropertyContextElement] = None

    /**
     * Holds an optional `AfterPropertyContextElement` that contains logic to be executed after the property-based test.
     * This private member is initialized to `None` and can be set to a specific `AfterPropertyContextElement` as
     * needed.
     */
    private var _afterPropertyContextElement: Option[AfterPropertyContextElement] = None

    /** Returns the number of evaluations performed in the current test context.
     *
     * @return The current number of evaluations as an `Int`.
     */
    def evaluations: Int = _evaluations

    /** Returns the context element for handling actions before the property-based test.
     *
     * @return An `Option[BeforePropertyContextElement]` containing the `BeforePropertyContextElement`, or `None` if it
     *         is not defined.
     */
    def beforePropertyContextElement: Option[BeforePropertyContextElement] = _beforePropertyContextElement

    /**
     * Sets the `BeforePropertyContextElement` for this context.
     *
     * @param element The `BeforePropertyContextElement` to be set, containing the actions to be performed before the
     *                test.
     */
    def beforePropertyContextElement_=(element: BeforePropertyContextElement): Unit =
        _beforePropertyContextElement = Some(element)

    /** Returns the context element for handling actions after the property-based test.
     *
     * @return An `Option[AfterPropertyContextElement]` containing the `AfterPropertyContextElement`, or `None` if it is
     *         not defined.
     */
    def afterPropertyContextElement: Option[AfterPropertyContextElement] = _afterPropertyContextElement

    /**
     * Sets the `AfterPropertyContextElement` for this context.
     * @param element The `AfterPropertyContextElement` to be set, containing the actions to be performed after the
     *                test.
     */
    def afterPropertyContextElement_=(element: AfterPropertyContextElement): Unit =
        _afterPropertyContextElement = Some(element)

    def attempts: Int = ???

    def labels: Set[Label] = ???

    def statistics: Map[Option[Label], Map[Option[Any], Int]] = ???

    def markSuccess(): Unit = ???

    /**
     * Handles the actions to be performed after a successful property-based test.
     *
     * The `onSuccess` method is called when a property-based test has successfully completed. It performs several
     * tasks, including outputting statistical information, handling classifications, and checking specific constraints
     * to ensure the test's validity. Finally, it clears any recorded failed seeds if applicable.
     *
     * @param numArgs The number of arguments used in the property test. If more than one argument was used, additional
     *                checks on discarded tests are performed.
     * @param randomSource The `RandomSource` used during the test, which contains the seed value for reproducibility.
     */
    def onSuccess(numArgs: Int, randomSource: RandomSource): Unit = {
        outputStatistics(this, numArgs, Success)
        outputClassifications(numArgs, configuration, randomSource.seed)
        checkMinSuccessful(configuration, randomSource.seed)
        if numArgs > 1 then checkMaxDiscarded()
        clearFailedSeeds()
    }

    def markFailure(e: Throwable): Unit = ???

    def classify[T](value: T, label: String): Unit = ???

    /** Increments the evaluation count by one.
     *
     * The `markEvaluation` method is used to track the progress of the property-based test by incrementing the
     * `_evaluations` counter each time it is called.
     */
    def markEvaluation(): Unit = _evaluations += 1

    /** Sets up the random source for the current test context and clears any generated samples.
     *
     * The `setupContextual` method initializes the contextual random source for the test, ensuring that the test uses
     * a consistent random source. It also clears the `_generatedSamples` sequence to prepare for new samples to be
     * generated in the current context.
     *
     * @param rs The `RandomSource` to be used as the contextual random source for the current test.
     */
    private[property] def setupContextual(rs: RandomSource): Unit = {
        _contextualRandomSource = Some(rs)
        _generatedSamples.clear()
    }
}
