/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.checkall
package arbitrary.shrinkers

import arbitrary.generators.Sample
import context.PropertyContext
import stacktraces.PropertyCheckStackTraces
import utils.RTree

import cl.ravenhill.munit.print.Print.printed

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

/**
 * A trait for defining shrinking behavior in property-based testing.
 *
 * The `Shrinker` trait is used to define a strategy for "shrinking" values in property-based testing.
 * Shrinking is the process of reducing a complex or large failing test case to a simpler or smaller one that still
 * fails. This helps in isolating the minimal failing case, making it easier to understand and debug the problem.
 *
 * @tparam A The type of value that can be shrunk by this shrinker.
 */
trait Shrinker[A] {

    /**
     * Shrinks a given value to a sequence of smaller or simpler values.
     *
     * The `shrink` method is responsible for generating a sequence of candidate values that are simpler or smaller
     * than the original value, yet still relevant for testing. These values are used to find the minimal failing case
     * in property-based testing.
     *
     * @param value The value to be shrunk.
     * @return A sequence of shrunk values.
     */
    def shrink(value: A): Seq[A]

    /**
     * Generates an `RTree` for the given value.
     *
     * @param value The value of type `A` for which the `RTree` should be generated.
     * @return An `RTree[A]` containing the provided value as the root of the tree.
     */
    def rtree(value: A): RTree[A] = {
        val fn = () => value
        rtree(fn)
    }


    /**
     * Generates an `RTree` for a value using a provided function.
     *
     * The `rtree` method creates a shrink tree (`RTree`) by using a function that generates a value of type `A`. The
     * method takes a function that produces a value and constructs an `RTree` where the root is the value produced by
     * the function, and the branches represent the possible shrinks of that value.
     *
     * @param fn A function that produces a value of type `A`. This function is called to generate the root value of the
     *           `RTree`.
     * @return An `RTree[A]` containing the value produced by the function as the root, with its possible shrinks as
     *         branches.
     */
    def rtree(fn: () => A): RTree[A] = {
        RTree(
            fn,
            () => {
                val value = fn()
                shrink(value).distinct.filter(_ != value).map(rtree)
            }
        )
    }
}

object Shrinker {

    /**
     * Generates a sequence of shrinking results for a given sample using the provided property function and shrinking
     * mode.
     *
     * The `shrinkFnFor` function takes a sample and a property function, then generates potential shrinks of the
     * sample. It applies both standard and contextual shrinking strategies based on the provided `ShrinkingMode`, and
     * returns a sequence of `ShrinkResult` instances representing the outcomes of the shrinking process.
     *
     * @param sample        The initial `Sample[T]` to be shrunk.
     * @param propertyFn    A function that tests the property on the given sample value.
     * @param shrinkingMode The mode that determines how shrinking should be applied.
     * @param seed          The seed used to ensure reproducibility during the shrinking process.
     * @param context       The property context in which the shrinking is performed.
     * @tparam T The type of the value being tested and shrunk.
     * @return A sequence of `ShrinkResult[T]` representing the results of shrinking the sample.
     */
    def shrinkFnFor[T](
        sample: Sample[T],
        propertyFn: T => Try[Unit],
        shrinkingMode: ShrinkingMode,
        seed: Long
    )(
        using context: PropertyContext, config: PropTestConfig, stackTraces: PropertyCheckStackTraces
    ): () => Try[Seq[ShrinkResult[?]]] = () => {
        // Copy the context to avoid modifying the original
        given PropertyContext = context.copy()

        // Define a property function that sets up the contextual random source before testing the value
        val property: (PropertyContext, T) => Unit = (ctx, value) => {
            ctx.setupContextual(RandomSource.seeded(seed))
            propertyFn(value)
        }
        // Perform standard shrinking on the sample's shrinks
        val smallerA = doShrinking(sample.shrinks, shrinkingMode)(propertyFn)
        // Perform contextual shrinking based on the shrinking mode
        val smallestContextual = doContextualShrinking(shrinkingMode)(property)
        // Combine the results of standard and contextual shrinking
        smallerA.map(_ :: smallestContextual).map(_.toSeq)
    }

    /**
     * Performs the shrinking process on a given initial value, attempting to find the smallest input that still causes
     * the property to fail.
     *
     * The `doShrinking` method initiates the shrinking process by taking an initial value wrapped in an `RTree` and
     * recursively testing smaller candidates. The goal is to find the smallest value that still results in a test
     * failure. The process is guided by the provided `ShrinkingMode`, which determines how many shrinking steps are
     * allowed.
     *
     * @param initial       The initial `RTree` containing the value to be shrunk and its children (potential shrinks).
     * @param shrinkingMode The `ShrinkingMode` that controls how many shrinking attempts are allowed.
     * @param test          A function that tests a value and returns a `Try[Unit]`, indicating success or failure.
     * @param stackTraces   The implicit `PropertyCheckStackTraces` used for stack trace analysis and error reporting.
     * @tparam T The type of the value being shrunk.
     * @return A `Try[ShrinkResult[T]]` containing the result of the shrinking process. The `ShrinkResult` includes the
     *         initial value, the smallest failing value found (or the initial value if no failures occurred), and an
     *         optional throwable that caused the failure.
     */
    private def doShrinking[T](initial: RTree[T], shrinkingMode: ShrinkingMode)(test: T => Try[Unit])
        (using stackTraces: PropertyCheckStackTraces): Try[ShrinkResult[T]] = Try {
        initial.children() match {
            case Nil =>
                // No children to shrink, so return the initial value as both the original and shrunk result
                ShrinkResult(initial.value(), initial.value(), None)
            case _ =>
                val counter = Counter()
                val tested = ListBuffer.empty[T]
                val stringBuilder = new StringBuilder
                stringBuilder.append(s"Attempting to shrink arg ${printed(initial.value()).get}\n")

                // Perform the shrinking step
                val stepResult = doStep(initial, shrinkingMode, tested, counter, test, stringBuilder)
                // Log the result of the shrinking process, interrupting if a failure occurs
                result(stringBuilder, stepResult, counter.value).get

                stepResult match {
                    case None => ShrinkResult(initial.value(), initial.value(), None)
                    case Some((failed, cause)) => ShrinkResult(initial.value(), failed, cause)
                }
        }
    }

    private def doContextualShrinking[T](shrinkingMode: ShrinkingMode)
        (property: (PropertyContext, T) => Unit): List[ShrinkResult[T]] = ???

    /**
     * Performs a single step in the shrinking process, attempting to find a smaller failing input.
     *
     * The `doStep` method executes one iteration of the shrinking process. It takes an initial tree of candidate values
     * and tests each candidate to see if it fails the property test. If a candidate fails, the method attempts to
     * shrink it further. The process continues until no more candidates are available or the shrinking mode's limit is
     * reached.
     *
     * @param initial       The initial `RTree` containing the value to be shrunk and its children (potential shrinks).
     * @param shrinkingMode The `ShrinkingMode` that controls how many shrinking attempts are allowed.
     * @param tested        A `ListBuffer` containing the values that have already been tested to avoid duplicates.
     * @param counter       A `Counter` to track the number of shrinking attempts made.
     * @param test          A function that tests a value and returns a `Try[Unit]`, indicating success or failure.
     * @param stringBuilder A `StringBuilder` used to accumulate detailed logs of the shrinking process.
     * @return An `Option[(T, Option[Throwable])]` representing the result of the shrinking step:
     *         - `Some((failedValue, Some(cause)))` if a candidate fails and cannot be shrunk further.
     *         - `None` if no more candidates are available or if shrinking is not allowed by the shrinking mode.
     */
    private def doStep[T](
        initial: RTree[T],
        shrinkingMode: ShrinkingMode,
        tested: ListBuffer[T],
        counter: Counter,
        test: T => Try[Unit],
        stringBuilder: StringBuilder
    ): Option[(T, Option[Throwable])] = {

        // Check if the shrinking mode allows further shrinking
        if (!shrinkingMode.isShrinking(counter.value)) {
            None
        } else {
            // Get the list of candidate values to be tested
            val candidates: Seq[RTree[T]] = initial.children()
            processCandidates(candidates, shrinkingMode, tested, counter, test, stringBuilder)
        }
    }

    /** Process the candidates for shrinking, testing each one. */
    private def processCandidates[T](
        candidates: Seq[RTree[T]],
        shrinkingMode: ShrinkingMode,
        tested: ListBuffer[T],
        counter: Counter,
        test: T => Try[Unit],
        stringBuilder: StringBuilder
    ): Option[(T, Option[Throwable])] = {

        candidates.view
            .filterNot(candidate => isAlreadyTested(candidate, tested))
            .foreach { candidate =>
                val value = candidate.value()
                counter.increment()
                testCandidate(value, candidate, shrinkingMode, tested, counter, test, stringBuilder)
            }
        None
    }

    /** Check if a candidate has already been tested. */
    private def isAlreadyTested[T](candidate: RTree[T], tested: ListBuffer[T]): Boolean = {
        val value = candidate.value()
        tested.contains(value) || {
            tested += value;
            false
        }
    }

    /** Test a candidate and handle the result. */
    private def testCandidate[T](
        value: T,
        candidate: RTree[T],
        shrinkingMode: ShrinkingMode,
        tested: ListBuffer[T],
        counter: Counter,
        test: T => Try[Unit],
        stringBuilder: StringBuilder
    ): Option[(T, Option[Throwable])] = {

        test(value) match {
            case Success(_) =>
                logShrinkStep(value, counter, passed = true, stringBuilder)
                None

            case Failure(t) =>
                logShrinkStep(value, counter, passed = false, stringBuilder)
                shrinkFurther(candidate, shrinkingMode, tested, counter, test, stringBuilder)
                    .orElse(Some((value, Some(t))))
        }
    }

    /** Log the result of a shrink step. */
    private def logShrinkStep[T](
        value: T,
        counter: Counter,
        passed: Boolean,
        stringBuilder: StringBuilder
    ): Try[Unit] = Try {
        if (PropertyTesting.shouldPrintShrinkSteps) {
            val result = if (passed) "pass" else "fail"
            stringBuilder.append(s"Shrink #${counter.value}: ${printed(value).get.value.get} $result\n")
        }
    }

    /** Attempt to shrink the candidate further if it failed. */
    private def shrinkFurther[T](
        candidate: RTree[T],
        shrinkingMode: ShrinkingMode,
        tested: ListBuffer[T],
        counter: Counter,
        test: T => Try[Unit],
        stringBuilder: StringBuilder
    ): Option[(T, Option[Throwable])] = {
        doStep(candidate, shrinkingMode, tested, counter, test, stringBuilder)
    }

    /**
     * Processes the result of a shrinking attempt and appends relevant information to a `StringBuilder`.
     *
     * @param stringBuilder The `StringBuilder` to which the shrinking result information will be appended.
     * @param stepResult    An optional tuple containing the failed value and an optional `Throwable` cause. If `None`,
     *                      it indicates that no shrinking steps were taken.
     * @param count         The number of shrinking steps that were attempted.
     * @param stackTraces   Implicit instance of `PropertyCheckStackTraces` used to retrieve stack trace information.
     * @return A `Try[Unit]` indicating the success or failure of the operation. If any errors occur while processing
     *         the result or appending stack trace information, they will be captured in the `Try`.
     */
    private def result[T](
        stringBuilder: StringBuilder,
        stepResult: Option[(T, Option[Throwable])],
        count: Int
    )(using stackTraces: PropertyCheckStackTraces): Try[Unit] = Try {
        stepResult match {
            // Case when no shrinking was performed
            case None | Some((_, None)) if count == 0 =>
                stringBuilder.append("No shrinking steps were taken\n")

            // Case when shrinking was performed and resulted in failure
            case Some((failed, causeOpt)) =>
                stringBuilder.append(s"Shrink result (after $count shrinks) => ${printed(failed).get.value}\n\n")
                appendStackTraceInfo(stringBuilder, causeOpt)

            // Case when the stepResult is not applicable, no action is needed
            case _ =>
        }

        // Optionally print the shrinking steps if enabled in configuration
        if (PropertyTesting.shouldPrintShrinkSteps) {
            println(stringBuilder.toString())
        }
    }

    /** Appends the stack trace information to the StringBuilder if available. */
    private def appendStackTraceInfo(
        stringBuilder: StringBuilder,
        causeOpt: Option[Throwable]
    )(using stackTraces: PropertyCheckStackTraces): Unit = {
        causeOpt.foreach { cause =>
            stackTraces.throwableLocation(Try(cause), 4) match {
                case Failure(exception) =>
                    stringBuilder.append(s"Caused by $exception\n")
                case Success(location) =>
                    stringBuilder.append(s"Caused by $cause at\n")
                    location.foreach(line => stringBuilder.append(s"\t$line\n"))
            }
        }
    }
}

/**
 * A simple counter class to keep track of an integer count.
 *
 * The `Counter` class provides a way to maintain a mutable count that can be incremented and accessed. It encapsulates
 * the count value and provides methods to modify and retrieve it.
 *
 * @constructor Creates a new `Counter` instance with an initial count of 0.
 */
private class Counter {

    /** The current count value. */
    private var count = 0

    /**
     * Increments the count by 1.
     *
     * The `increment` method increases the current count value by 1.
     */
    def increment(): Unit = count += 1

    /**
     * Retrieves the current count value.
     *
     * The `value` method returns the current value of the count.
     *
     * @return The current count as an `Int`.
     */
    def value: Int = count
}
