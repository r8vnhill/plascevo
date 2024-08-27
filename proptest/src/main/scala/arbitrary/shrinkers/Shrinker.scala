/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.checkall
package arbitrary.shrinkers

import arbitrary.generators.Sample
import context.PropertyContext
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
    )(using context: PropertyContext, config: PropTestConfig): () => Seq[ShrinkResult[?]] = () => {
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
        smallerA :: smallestContextual
    }

    /**
     * Performs the shrinking process for a given value, attempting to find a smaller value that still fails the test.
     *
     * The `doShrinking` method takes an initial `RTree` of values and applies the provided test function to each
     * possible smaller value, as determined by the shrinking mode. The goal of shrinking is to find the simplest value
     * that still causes the test to fail. This method returns a `ShrinkResult` containing the original value, the
     * smallest failing value found, and any associated exception that caused the failure.
     *
     * @param initial       The initial `RTree[T]` containing the value to be shrunk and its possible smaller values.
     * @param shrinkingMode The mode that determines how the shrinking process should proceed.
     * @param test          A function that tests the value. If the test fails, the shrinking process continues.
     * @tparam T The type of the value being shrunk.
     * @return A `ShrinkResult[T]` containing the original value, the smallest failing value found (if any), and the
     *         exception that caused the failure, if applicable.
     */
    private def doShrinking[T](initial: RTree[T], shrinkingMode: ShrinkingMode)(test: T => Try[Unit]): ShrinkResult[T] =
        initial.children() match {
            case Nil => ShrinkResult(initial.value(), initial.value(), None)
            case _ =>
                val counter = Counter()
                val tested = ListBuffer.empty[T]
                val stringBuilder = new StringBuilder
                stringBuilder.append(s"Attempting to shrink arg ${printed(initial.value()).get}\n")

                val stepResult = doStep(initial, shrinkingMode, tested, counter, test, stringBuilder)
                result(stringBuilder, stepResult, counter.value)

                stepResult match {
                    case None => ShrinkResult(initial.value(), initial.value(), None)
                    case Some((failed, cause)) => ShrinkResult(initial.value(), failed, cause)
                }
        }

    private def doContextualShrinking[T](shrinkingMode: ShrinkingMode)
        (property: (PropertyContext, T) => Unit): List[ShrinkResult[T]] = ???

    private def doStep[T](
        initial: RTree[T],
        shrinkingMode: ShrinkingMode,
        tested: ListBuffer[T],
        counter: Counter,
        test: T => Try[Unit],
        stringBuilder: StringBuilder
    ): Option[(T, Option[Throwable])] = {

        if (!shrinkingMode.isShrinking(counter.value)) {
            None
        } else {
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
        tested.contains(value) || { tested += value; false }
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
    ): Unit = {
        if (PropertyTesting.shouldPrintShrinkSteps) {
            val result = if (passed) "pass" else "fail"
            stringBuilder.append(s"Shrink #${counter.value}: ${printed(value).get.value} $result\n")
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

    private def result[T](
        stringBuilder: StringBuilder,
        stepResult: Option[(T, Option[Throwable])],
        counter: Int
    ): Unit = ???
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
