/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.checkall
package context

import arbitrary.generators.Sample
import classifications.Output.outputClassifications
import internal.Checks.{checkMaxDiscarded, checkMinSuccessful}
import statistics.Label
import statistics.Output.outputStatistics
import utils.TestResult

import cl.ravenhill.munit.collectors.ErrorCollector

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

/**
 * Manages the state and lifecycle of a property-based test.
 *
 * The `PropertyContext` class provides the necessary context for executing property-based tests. It tracks the number
 * of evaluations performed during the test, manages the random source specific to the current test context, and handles
 * generated samples. This context is essential for coordinating the execution flow of property tests, including setting
 * up the random source and marking evaluations.
 *
 * @param testName      The name of the property-based test.
 * @param configuration The configuration settings for the property-based testing process, provided implicitly.
 */
final case class PropertyContext(testName: String)(using val configuration: PropTestConfig = PropTestConfig()) {

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

    /** Tracks the number of successful evaluations in the property-based test; initialized to 0. */
    private var _successes: Int = 0

    /** Tracks the number of failed evaluations in the property-based test; initialized to 0. */
    private var _failures: Int = 0

    /** A mutable map storing classifications for property-based testing. */
    private val _classifications: mutable.Map[Option[Label], mutable.Map[Option[Any], Int]] = mutable.Map.empty

    /** A private, mutable map that tracks automatic classifications of test results. */
    private val _autoClassifications: mutable.Map[String, mutable.Map[String, Int]] = mutable.Map.empty

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
     *
     * @param element The `AfterPropertyContextElement` to be set, containing the actions to be performed after the
     *                test.
     */
    def afterPropertyContextElement_=(element: AfterPropertyContextElement): Unit =
        _afterPropertyContextElement = Some(element)

    /** Returns the number of successful evaluations in the property-based test.
     *
     * @return The number of successful evaluations as an `Int`.
     */
    def successes: Int = _successes

    /** Returns the number of failed evaluations in the property-based test.
     *
     * @return The number of failed evaluations as an `Int`.
     */
    def failures: Int = _failures

    /**
     * Returns the total number of test attempts made during the property-based test.
     *
     * The `attempts` method calculates and returns the sum of successful and failed evaluations in the current
     * property-based test. This value represents the total number of test cases that have been executed, regardless
     * of whether they passed or failed.
     */
    def attempts: Int = _successes + _failures

    /** Retrieves the set of all distinct labels used in the property-based test. */
    def labels: Set[Label] = statistics.keys
        .filter(_.isDefined)
        .map(_.get) // Safe to call get since we know the label is defined
        .toSet

    /**
     * Retrieves a snapshot of the current test case classifications and their associated counts.
     *
     * The `statistics` method converts the internal mutable map `_classifications` into an immutable `Map`, providing a
     * snapshot of the classifications and their associated counts at the current state of the property-based test. Each
     * entry in the map corresponds to an optional `Label`, which may be associated with multiple test case values
     * (represented as `Option[Any]`), along with the count of how many times each value has been classified under that
     * label.
     *
     * @return An immutable `Map` representing the classifications of test cases and their respective counts.
     */
    def statistics: Map[Option[Label], Map[Option[Any], Int]] = _classifications.view.mapValues(_.toMap).toMap

    /**
     * Retrieves a snapshot of the current automatic classifications as an immutable map.
     *
     * @return An immutable `Map[String, Map[String, Int]]` that reflects the current state of automatic
     *         classifications.
     */
    def autoClassifications: Map[String, Map[String, Int]] = _autoClassifications.view.mapValues(_.toMap).toMap

    /**
     * Retrieves the sequence of generated samples within the current property context.
     *
     * @return A sequence of `Sample` objects representing the values generated within the property context.
     */
    def generatedSamples: Seq[Sample[?]] = _generatedSamples.toSeq

    def markSuccess(): Unit = ???

    /**
     * Handles the actions to be performed after a successful property-based test.
     *
     * The `onSuccess` method is called when a property-based test has successfully completed. It performs several
     * tasks, including outputting statistical information, handling classifications, and checking specific constraints
     * to ensure the test's validity. Finally, it clears any recorded failed seeds if applicable.
     *
     * @param numArgs      The number of arguments used in the property test. If more than one argument was used,
     *                     additional checks on discarded tests are performed.
     * @param randomSource The `RandomSource` used during the test, which contains the seed value for reproducibility.
     * @param context      The implicit `PropertyContext` for the test, which tracks the test's progress and results.
     */
    def onSuccess(numArgs: Int, randomSource: RandomSource)
        (using context: PropertyContext, configuration: PropTestConfig, errorCollector: ErrorCollector): Try[Unit] = {
        outputStatistics(this, numArgs, TestResult.Passed)
        outputClassifications(numArgs, configuration, randomSource.seed)
        checkMinSuccessful(randomSource.seed) match {
            case Success(_) =>
                if numArgs > 1 then
                    checkMaxDiscarded()
                    Success(())
                else
                    Success(())
            case Failure(exception) => Failure(exception)
        }
    }

    /**
     * Increments the failure count and records the occurrence of a test failure.
     *
     * The `markFailure` method is called when a test fails, incrementing the internal failure counter. This allows the
     * `PropertyContext` to keep track of how many tests have failed during the execution of property-based tests.
     */
    def markFailure(): Unit = _failures += 1

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
    private[checkall] def setupContextual(rs: RandomSource): Unit = {
        _contextualRandomSource = Some(rs)
        _generatedSamples.clear()
    }
}
