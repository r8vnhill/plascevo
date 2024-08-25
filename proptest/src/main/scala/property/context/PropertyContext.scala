/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.context

import property.arbitrary.generators.Sample
import property.{PropTestConfig, RandomSource}

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
 * @param config The configuration settings for the property-based testing process, provided implicitly.
 */
final class PropertyContext(using val config: PropTestConfig = PropTestConfig()) {

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
        
    def markSuccess(): Unit = ???
    
    def onSuccess(args: Int, random: RandomSource): Unit = ???

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
