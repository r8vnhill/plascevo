/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.checkall
package arbitrary.shrinkers

import munit.checkall.arbitrary.generators.Sample
import munit.checkall.context.PropertyContext
import munit.checkall.utils.RTree

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

    def shrinkFnFor[T](
        sample: Sample[T],
        propertyFn: (T) => Unit,
        mode: ShrinkingMode,
        seed: Long
    )(context: PropertyContext): Seq[ShrinkResult[T]] = {
        val property: (PropertyContext, T) => Unit = (ctx, value) => {
            ???
        }
        ???
    }
}
