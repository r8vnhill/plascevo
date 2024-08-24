/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package genetics.chromosomes

import exceptions.InvalidSizeException

import cl.ravenhill.composerr.constrained
import cl.ravenhill.composerr.constraints.iterable.HaveSize

import scala.collection.mutable

/** Utility functions for managing chromosome constraints and adjustments in evolutionary algorithms.
 *
 * The `ChromosomeUtils` object provides utility methods that are specifically designed to handle constraints and
 * adjustments related to chromosomes within an evolutionary algorithm. These methods ensure that the ranges and filters
 * applied to genes within a chromosome are consistent and conform to expected sizes, thereby maintaining the integrity
 * of the chromosome structure during initialization and mutation processes.
 *
 * The methods in this object are intended for internal use within the chromosome package, helping to enforce
 * constraints and adjust configurations as necessary.
 */
private[chromosomes] object ChromosomeUtils {

    /** Enforces size constraints on the provided ranges and filters for a chromosome.
     *
     * This method checks that the sizes of the `ranges` and `filters` sequences match the specified chromosome `size`,
     * particularly when multiple ranges or filters are provided. If there are multiple ranges or filters, their sizes
     * must match the number of genes in the chromosome. If these constraints are violated, a `CompositeException` is
     * thrown with an `InvalidSizeException` for each constraint that is not met.
     *
     * @param ranges  A sequence of ranges defining the bounds for gene values in the chromosome.
     * @param filters A sequence of filters to apply to the genes in the chromosome.
     * @param size    The expected size of the chromosome (i.e., the number of genes).
     * @throws InvalidSizeException if the size of ranges or filters does not match the chromosome size when there
     *                              are multiple ranges or filters.
     */
    def enforceConstraints[T](ranges: mutable.Seq[(T, T)], filters: mutable.Seq[T => Boolean], size: Int): Unit = {
        constrained {
            if (ranges.size > 1) {
                "Chromosome with multiple ranges must have the same number of genes as ranges." ~ (
                    ranges must HaveSize {
                        _ == size
                    },
                    InvalidSizeException(_)
                )
            }
            if (filters.size > 1) {
                "Chromosome with multiple filters must have the same number of genes as filters." ~ (
                    filters must HaveSize {
                        _ == size
                    },
                    InvalidSizeException(_)
                )
            }
        }
    }

    /** Adjusts the ranges and filters for a chromosome, ensuring they match the chromosome size.
     *
     * This method adjusts the `ranges` and `filters` sequences to ensure they match the size of the chromosome. If no
     * ranges or filters are provided, or if a single range or filter is provided, the method replicates the default
     * range or filter across all genes in the chromosome. This ensures that every gene has an associated range and
     * filter.
     *
     * @param size          The expected size of the chromosome (i.e., the number of genes).
     * @param ranges        A sequence of ranges defining the bounds for gene values, or an empty sequence.
     * @param defaultRange  The default range to use if no ranges are provided.
     * @param filters       A sequence of filters to apply to the genes, or an empty sequence.
     * @param defaultFilter The default filter to use if no filters are provided.
     * @return A tuple containing the adjusted ranges and filters sequences.
     */
    def adjustRangesAndFilters[T](
        size: Int,
        ranges: mutable.Seq[(T, T)],
        defaultRange: (T, T),
        filters: mutable.Seq[T => Boolean],
        defaultFilter: T => Boolean
    ): (mutable.Seq[(T, T)], mutable.Seq[T => Boolean]) = {

        def adjust[A](seq: mutable.Seq[A], default: A): mutable.Seq[A] = seq match {
            case Nil => mutable.Seq.fill(size)(default)
            case mutable.Seq(single) => mutable.Seq.fill(size)(single)
            case _ => seq
        }

        val adjustedRanges = adjust(ranges, defaultRange)
        val adjustedFilters = adjust(filters, defaultFilter)

        (adjustedRanges, adjustedFilters)
    }
}
