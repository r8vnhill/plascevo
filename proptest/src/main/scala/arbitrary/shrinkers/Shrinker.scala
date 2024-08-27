/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.checkall
package arbitrary.shrinkers

import arbitrary.generators.Sample
import context.PropertyContext
import utils.RTree

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
        propertyFn: T => Unit,
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

    private def doShrinking[T](initial: RTree[T], shrinkingMode: ShrinkingMode)(test: T => Unit): ShrinkResult[T] = ???

    private def doContextualShrinking[T](shrinkingMode: ShrinkingMode)
        (property: (PropertyContext, T) => Unit): List[ShrinkResult[T]] = ???
}
