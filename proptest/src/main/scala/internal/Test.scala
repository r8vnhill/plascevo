/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.checkall
package internal

import arbitrary.Classifier
import arbitrary.shrinkers.ShrinkResult
import context.PropertyContext
import internal.Exceptions.createPropertyFailExceptionWithResults
import stacktraces.PropertyCheckStackTraces
import statistics.Output.outputStatistics
import utils.TestResult

import cl.ravenhill.composerr.Constrained
import cl.ravenhill.composerr.Constrained.constrained
import cl.ravenhill.composerr.constraints.iterable.HaveSize
import cl.ravenhill.munit.collectors.ErrorCollector

import scala.util.{Failure, Success, Try}

/**
 * A utility object for running and managing property-based tests.
 *
 * The `Test` object provides methods for executing property-based tests within a controlled context. It ensures that
 * the test inputs are validated, classified, and processed correctly, handles exceptions, and manages the lifecycle of
 * the property-based test. The object also handles shrinking of failing inputs and provides detailed error reporting
 * when tests fail.
 *
 * This object is designed to be used internally within the `checkall` package, providing a consistent and
 * well-structured approach to running property-based tests with error handling and classification.
 */
private[checkall] object Test {

    /**
     * Executes a property-based test with the provided configuration and input data.
     *
     * The `apply` method orchestrates the execution of a property-based test by validating the inputs, setting up the
     * test context, running the test function, and handling any failures that occur during the test. It ensures that
     * inputs and classifiers are correctly matched, manages the lifecycle of the test context, and handles exceptions
     * by attempting to shrink the failing inputs.
     *
     * @param config         The `PropTestConfig` instance containing configuration settings for the property-based
     *                       test, such as the maximum number of failures allowed and the shrinking mode.
     * @param shrinkFn       A function that generates a sequence of `ShrinkResult` instances, representing the
     *                       shrinking process for failing inputs.
     * @param inputs         A sequence of input values that are passed to the test function. Each input value may be
     *                       classified based on the provided classifiers.
     * @param classifiers    A sequence of optional classifiers, each corresponding to an input value. Classifiers are
     *                       used to categorize input values during the test.
     * @param seed           The seed used for the random number generator, which allows the test to be reproduced.
     * @param contextualSeed The seed used for setting up the contextual random source, ensuring that the random number
     *                       generation is consistent within the context of the test.
     * @param testFunction   The test function to be executed. This is a parameterless function that contains the logic
     *                       for the property-based test.
     * @param context        An implicit `PropertyContext` instance that manages the lifecycle and state of the
     *                       property-based test.
     * @param errorCollector An implicit `ErrorCollector` instance that collects and reports errors encountered during
     *                       the test.
     * @param stackTraces    An implicit `PropertyCheckStackTraces` instance used to analyze and report stack traces for
     *                       errors encountered during the test.
     * @return A `Try[Unit]` representing the result of the test execution. If the test passes without any errors, the
     *         result is a `Success(Unit)`. If an exception is encountered, the result is a `Failure` containing the
     *         exception and any associated shrinking results.
     */
    def apply(
        config: PropTestConfig,
        shrinkFn: () => Seq[ShrinkResult[?]],
        inputs: Seq[Any],
        classifiers: Seq[Option[Classifier[?]]],
        seed: Long,
        contextualSeed: Long,
    )(testFunction: => Any)(
        using
        context: PropertyContext,
        errorCollector: ErrorCollector,
        stackTraces: PropertyCheckStackTraces
    ): Try[Unit] = {
        // Validate that the inputs and classifiers have matching sizes
        validateInputsAndClassifiers(inputs, classifiers)

        // Mark the test for evaluation and set up the contextual random source
        context.markEvaluation()
        context.setupContextual(RandomSource.seeded(contextualSeed))

        // Execute the test function and handle any potential failures
        Try {
            classifyInputs(context, inputs, classifiers)
            handlePropertyContext(context)(testFunction)
        }.recover {
            case e: AssumptionFailedException =>
            // Ignore assumption failures; they do not mark the test as a failure
            case e: Throwable =>
                // Handle other exceptions by marking the test as a failure and initiating shrinking
                handleFailure(shrinkFn, inputs, seed, e, config)
        }
    }

    /**
     * Validates that the size of the `inputs` sequence matches the size of the `classifiers` sequence.
     *
     * This method checks whether the number of elements in the `inputs` sequence is equal to the number of elements
     * in the `classifiers` sequence. If the sizes do not match, a constraint violation is raised.
     *
     * @param inputs      The sequence of input values to be classified. Each element in this sequence should correspond to
     *                    an element in the `classifiers` sequence.
     * @param classifiers The sequence of optional classifiers. Each classifier is responsible for categorizing the
     *                    corresponding input value. The sequence should have the same size as the `inputs` sequence.
     * @throws CompositeException          containing all the constraints that failed.
     * @throws IterableConstraintException if the size of the `inputs` sequence does not match the size of the
     *                                     `classifiers` sequence; wrapped in a `CompositeException`.
     */
    private def validateInputsAndClassifiers(
        inputs: Seq[Any],
        classifiers: Seq[Option[Classifier[? <: Any]]]
    ): Unit = {
        constrained {
            "Inputs should have the same size as classifiers" | {
                inputs must HaveSize(_ == classifiers.size)
            }
        }
    }

    /**
     * Classifies the input values using the provided classifiers and updates the property context.
     *
     * This method iterates over the input values and their corresponding classifiers. For each input, if a classifier
     * is provided, the input value is classified by applying the classifier. The resulting classification label, if
     * present, is then used to update the `PropertyContext`. The classification is used to categorize the input values
     * based on certain criteria, which is essential for property-based testing to understand how different categories
     * of inputs behave.
     *
     * @param context     The `PropertyContext` used to store the classification results. This context keeps track of how
     *                    inputs are classified during the property test.
     * @param inputs      The sequence of input values to be classified. Each element corresponds to an element in the
     *                    `classifiers` sequence.
     * @param classifiers The sequence of optional classifiers. Each classifier is responsible for categorizing the
     *                    corresponding input value. If a classifier is `None`, no classification is performed for that
     *                    input.
     * @throws ClassCastException if the classifier cannot be cast to `Classifier[Any]`, which indicates a mismatch in
     *                            the expected type of the input value and the classifier.
     */
    private def classifyInputs(
        context: PropertyContext,
        inputs: Seq[Any],
        classifiers: Seq[Option[Classifier[? <: Any]]]
    ): Unit = {
        inputs.indices.foreach { k =>
            classifiers(k) match {
                case Some(c) =>
                    val value = inputs(k)
                    val label = c.asInstanceOf[Classifier[Any]](value)
                    label.foreach(l => context.classify(k, l))
                case None =>
            }
        }
    }

    /**
     * Manages the lifecycle of the property context around the execution of a test function.
     *
     * @param context      The `PropertyContext` that manages the lifecycle and state of the property-based test. This
     *                     context may contain custom elements that need to be executed before and after the test.
     * @param testFunction The test function to be executed within the managed context. This is a parameterless function 
     *                     that contains the test logic.
     */
    private def handlePropertyContext(context: PropertyContext)(testFunction: => Any): Unit = {
        context.beforePropertyContextElement.foreach(_.before())
        testFunction
        context.markSuccess()
        context.afterPropertyContextElement.foreach(_.after())
    }

    /**
     * Handles the failure scenario during property-based testing.
     *
     * @param shrinkFn A function that generates a sequence of `ShrinkResult` instances representing the shrinking
     *                 process.
     * @param inputs A sequence of input values that were used during the test when the failure occurred.
     * @param seed The seed used for the random number generator, which allows the test to be reproduced.
     * @param e The original exception that caused the test to fail.
     * @param config The configuration settings for the property-based test, including the maximum number of failures
     *               allowed.
     * @param context The `PropertyContext` instance that provides context for the current test execution.
     * @param errorCollector The `ErrorCollector` used to gather and report errors during the test.
     * @param stackTraces The `PropertyCheckStackTraces` used to analyze and report stack traces for errors.
     * @return A `Try[PropertyFailException]` representing either a successful creation of a `PropertyFailException` or
     *         the failure cause if the exception is not handled by the method.
     */
    private def handleFailure(
        shrinkFn: () => Seq[ShrinkResult[Any]],
        inputs: Seq[Any],
        seed: Long,
        e: Throwable,
        config: PropTestConfig
    )(using
        context: PropertyContext,
        errorCollector: ErrorCollector,
        stackTraces: PropertyCheckStackTraces
    ): Try[PropertyFailException] = {
        // Mark the test as a failure in the context
        context.markFailure()
        // Output statistics related to the failure
        outputStatistics(context = context, numArgs = inputs.size, success = TestResult.Failure)
        // Delegate further exception handling to the handleException method
        handleException(shrinkFn, inputs, seed, e, config)
    }

    /**
     * Handles exceptions that occur during property-based testing.
     *
     * @param shrinkFn       A function that generates a sequence of `ShrinkResult` instances, representing the
     *                       shrinking process.
     * @param inputs         A sequence of input values that were used during the test when the exception occurred.
     * @param seed           The seed used for the random number generator, which allows the test to be reproduced.
     * @param cause          The original exception that was thrown during the test.
     * @param config         The configuration settings for the property-based test, including the maximum number of
     *                       failures allowed.
     * @param context        The `PropertyContext` instance that provides context for the current test execution.
     * @param errorCollector The `ErrorCollector` used to gather and report errors during the test.
     * @param stackTraces    The `PropertyCheckStackTraces` used to analyze and report stack traces for errors.
     * @return A `Try[PropertyFailException]` representing either a successful creation of a `PropertyFailException` or
     *         the failure cause if the exception is not handled by the method.
     */
    private def handleException(
        shrinkFn: () => Seq[ShrinkResult[Any]],
        inputs: Seq[Any],
        seed: Long,
        cause: Throwable,
        config: PropTestConfig
    )(
        using
        context: PropertyContext,
        errorCollector: ErrorCollector,
        stackTraces: PropertyCheckStackTraces
    ): Try[PropertyFailException] = if (config.maxFailure == 0) {
        // If the maxFailure is set to 0, immediately print the failure message and throw the failure exception with
        // results
        printFailureMessage(inputs, cause)
        createPropertyFailExceptionWithResults(shrinkFn(), cause, context.attempts, seed)
    } else if (context.failures > config.maxFailure) {
        // If the number of failures exceeds maxFailure, build and throw an error with a detailed failure message
        val error = buildMaxErrorFailureMessage(context, config, inputs)
        createPropertyFailExceptionWithResults(shrinkFn(), AssertionError(error), context.attempts, seed)
    } else {
        // Otherwise, print the failure message and return a Failure with the exception
        printFailureMessage(inputs, cause)
        Failure(cause)
    }

    /**
     * Prints a detailed failure message for a property-based test that failed.
     *
     * The `printFailureMessage` method constructs and prints a failure message, which includes details about the inputs
     * that were tested, any additional samples generated within the property context, and the stack trace of the
     * exception that caused the failure. This information is valuable for debugging and understanding why a
     * property-based test failed.
     *
     * @param inputs  A sequence of inputs that were tested before the failure occurred.
     * @param e       The exception that caused the test to fail.
     * @param context The implicit `PropertyContext` that provides additional context for the property-based test,
     *                including any samples generated during the test.
     */
    private def printFailureMessage(
        inputs: Seq[Any],
        e: Throwable
    )(using context: PropertyContext, stackTraces: PropertyCheckStackTraces): Unit = {
        val sb = new StringBuilder

        // Append detailed information about the inputs and generated samples to the StringBuilder
        appendInputsInfo(sb, inputs)

        // Append the stack trace and exception cause to the StringBuilder
        appendStackTraceInfo(sb, e)

        // Print the final failure message
        println(sb.toString())
    }

    /** Appends formatted input information to the StringBuilder. */
    private def appendInputsInfo(sb: StringBuilder, inputs: Seq[Any])(using context: PropertyContext): Unit = {
        sb.append("Property test failed for inputs\n\n")

        val allInputs = gatherInputsAndSamples(inputs)

        allInputs.zipWithIndex.foreach { case (input, index) =>
            sb.append(s"$index) $input\n")
        }
        sb.append("\n")
    }

    /** Gathers inputs and generated samples into a single iterator. */
    private def gatherInputsAndSamples(inputs: Seq[Any])(using context: PropertyContext): Iterator[String] = {
        inputs.iterator.map(input => input.toString) ++
            context.generatedSamples.map(sample =>
                s"${sample.value} (generated within property context)"
            )
    }

    /** Appends the stack trace and exception cause to the StringBuilder. */
    private def appendStackTraceInfo(sb: StringBuilder, e: Throwable)
        (using stackTraces: PropertyCheckStackTraces): Unit = {
        val cause = Try(stackTraces.root(e))
        stackTraces.throwableLocation(cause, 4) match {
            case Failure(exception) => sb.append(s"Caused by $exception\n")
            case Success(stack) =>
                sb.append(s"Caused by $e at\n")
                stack.foreach { trace => sb.append(s"\t$trace\n") }
        }
        sb.append("\n")
    }

    private def buildMaxErrorFailureMessage(
        context: PropertyContext,
        config: PropTestConfig,
        inputs: Seq[Any]
    ): String = ???
}
