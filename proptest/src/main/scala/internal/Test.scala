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
import munit.internal.console.StackTraces

import scala.util.{Failure, Success, Try}

private[checkall] object Test {
    /**
     * Executes a property-based test, handling input classification, evaluation, and failure recovery.
     *
     * The `apply` method is a central point for executing a property-based test with specified inputs and classifiers.
     * It validates the inputs and classifiers, marks the test for evaluation, and sets up the contextual random source
     * based on the provided seed. The test function is executed within this context, and any failures are caught and
     * processed through the shrinking mechanism to simplify the failing case.
     *
     * @param context        The `PropertyContext` that manages the state and lifecycle of the property-based test.
     *                       This context is updated with the evaluation result, and any failure information.
     * @param config         The configuration object (`PropTestConfig`) that contains settings and options for the
     *                       property-based testing process. This configuration may influence how the test is executed
     *                       and how failures are handled.
     * @param shrinkFn       A function that generates potential shrinking results based on the provided
     *                       `PropertyContext`. Shrinking is the process of finding simpler inputs that still cause
     *                       the test to fail, helping to isolate the cause of the failure.
     * @param inputs         A sequence of inputs that are provided to the test function. These inputs are validated
     *                       and classified before the test is executed.
     * @param classifiers    A sequence of optional classifiers that correspond to the inputs. These classifiers are
     *                       used to categorize the inputs during the test execution.
     * @param seed           The seed value used for the random number generator during the test. This seed is
     *                       important for reproducibility.
     * @param contextualSeed A contextual seed used to initialize the random source specific to the current test
     *                       context.
     * @param testFunction   The test function that is executed as part of the property-based test. This function is
     *                       expected to contain the assertions or conditions being tested.
     * @return A `Try[Unit]` representing the result of the test execution. If the test function completes successfully,
     *         the result is `Success(())`. If an exception occurs during the test, the result is a `Failure` containing
     *         the exception.
     * @throws AssumptionFailedException If the test fails due to an unmet assumption. This exception is silently caught
     *                                   and does not mark the test as a failure.
     * @throws Throwable                 If any other exception occurs during the test execution, it is caught, and the test is marked
     *                                   as a failure. The exception is processed for shrinking and further analysis.
     */
    def apply(
        config: PropTestConfig,
        shrinkFn: PropertyContext => Seq[ShrinkResult[Any]],
        inputs: Seq[Any],
        classifiers: Seq[Option[Classifier[? <: Any]]],
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
     * This method is responsible for handling the setup and teardown actions associated with a property-based test. 
     * It interacts with the `PropertyContext` to perform any necessary preparations before the test, execute the test 
     * function, and then mark the test as successful if it completes without exceptions. After the test, it performs
     * any necessary cleanup actions. This function ensures that the test lifecycle is properly managed, which includes
     * invoking any custom behaviors defined in the context before and after the test execution.
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
     * Handles the processing and reporting of a test failure within a property-based testing context.
     *
     * This method is invoked when a property-based test encounters an exception or fails to meet its assertions. It
     * performs several key actions: it records the failure in the test context, outputs relevant statistics about the
     * test run, and initiates the shrinking process to find the minimal failing case. The function is designed to be a
     * central point for managing the aftermath of a test failure, ensuring that all necessary steps are taken to
     * document and analyze the failure.
     *
     * @param context     The `PropertyContext` that maintains the state of the property-based test. This context is
     *                    updated to reflect the failure of the test.
     * @param shrinkFn    A function that generates potential shrinking results based on the provided `PropertyContext`. 
     *                    Shrinking is the process of finding simpler inputs that still cause the test to fail, helping to 
     *                    isolate the cause of the failure.
     * @param inputs      A sequence of inputs that were provided to the test when the failure occurred. These inputs are 
     *                    crucial for reproducing and analyzing the failure.
     * @param seed        The seed value used for the random number generator during the test. This seed is important for 
     *                    reproducibility.
     * @param e           The exception that caused the test to fail. This exception is recorded and reported as part of
     *                    the failure handling process.
     * @param config      The configuration object (`PropTestConfig`) that contains settings and options for the
     *                    property-based testing process. This configuration may influence how failures are handled,
     *                    including how shrinking is performed.
     * @param stackTraces The stack traces utility that provides methods for analyzing and processing exceptions during
     *                    property-based testing.
     */
    private def handleFailure(
        shrinkFn: PropertyContext => Seq[ShrinkResult[Any]],
        inputs: Seq[Any],
        seed: Long,
        e: Throwable,
        config: PropTestConfig
    )(using context: PropertyContext, errorCollector: ErrorCollector, stackTraces: PropertyCheckStackTraces): Unit = {
        context.markFailure()
        outputStatistics(context = context, numArgs = inputs.size, success = TestResult.Failure)
        handleException(shrinkFn, inputs, seed, e, config)
    }

    /**
     * Handles exceptions that occur during property-based testing.
     *
     * The `handleException` method is responsible for managing exceptions that arise while running property-based
     * tests. Depending on the configuration and the context of the test, it either prints a detailed failure message
     * and throws an exception or checks if the number of failures exceeds the maximum allowed and throws a
     * corresponding failure exception.
     *
     * @param shrinkFn The function responsible for generating shrink results based on the current `PropertyContext`.
     * @param inputs   A sequence of inputs that were tested before the exception occurred.
     * @param seed     The random seed used for the test, which allows for reproducibility of the test case.
     * @param cause    The original exception that caused the test to fail.
     * @param config   The configuration settings for the property-based test, which dictate how failures should be
     *                 handled.
     * @param context  The implicit `PropertyContext` that provides additional context about the test, such as the number
     *                 of attempts and failures.
     * @throws Throwable Depending on the test configuration, the method may throw a `Throwable` containing detailed
     *                   information about the failure, including shrink results and the cause of the failure.
     */
    private[checkall] def handleException(
        shrinkFn: PropertyContext => Seq[ShrinkResult[Any]],
        inputs: Seq[Any],
        seed: Long,
        cause: Throwable,
        config: PropTestConfig
    )(
        using
        context: PropertyContext,
        errorCollector: ErrorCollector,
        stackTraces: PropertyCheckStackTraces
    ): Unit = if (config.maxFailure == 0) {
        // If the maxFailure is set to 0, immediately print the failure message and throw the failure exception with
        // results
        printFailureMessage(inputs, cause)
        createPropertyFailExceptionWithResults(shrinkFn(context), cause, context.attempts, seed)
    } else if (context.failures > config.maxFailure) {
        // If the number of failures exceeds maxFailure, build and throw an error with a detailed failure message
        val error = buildMaxErrorFailureMessage(context, config, inputs)
        createPropertyFailExceptionWithResults(shrinkFn(context), AssertionError(error), context.attempts, seed)
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
