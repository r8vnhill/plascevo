/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package genetics.chromosomes.numeric

import exceptions.InvalidSizeException
import genetics.chromosomes.{Chromosome, ChromosomeBuilder, ChromosomeUtils}
import genetics.genes.numeric.NumericGene

import cl.ravenhill.composerr.constrained
import cl.ravenhill.composerr.constraints.option.BeNone

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.Random

/** A builder for creating instances of `NumericChromosome` in an evolutionary algorithm.
 *
 * The `NumericChromosomeBuilder` trait extends the [[ChromosomeBuilder]] trait and provides additional methods for
 * specifying ranges and filters that apply to the numeric genes within a chromosome. This builder allows for the
 * construction of numeric chromosomes where each gene's value can be constrained by a range and filtered by a
 * predicate. The builder pattern is used, enabling a flexible and fluent interface for configuring the chromosome
 * before building it.
 *
 * @tparam T The type of numeric value stored by the genes within the chromosome.
 * @tparam G The type of gene contained in the chromosome, which must extend [[NumericGene]].
 *
 * @example
 * {{{
 * // Example usage of a NumericChromosomeBuilder for a chromosome of integer genes
 * val builder = new NumericChromosomeBuilder[Int, IntGene] {
 *   override protected def defaultRange: (Int, Int) = (0, 100)
 *
 *   override protected def createChromosome(
 *     ranges: Seq[(Int, Int)],
 *     filters: Seq[Int => Boolean]
 *   )(using random: Random): Chromosome[Int, IntGene] = {
 *     val genes = ranges.zip(filters).map { case ((min, max), filter) =>
 *       val value = random.nextInt(max - min + 1) + min
 *       IntGene(value)(filter)
 *     }
 *     new NumericChromosome[Int, IntGene](genes)
 *   }
 * }
 *
 * val chromosome = builder
 *   .withSize(10)
 *   .addRange((10, 50))
 *   .addFilter(_ % 2 == 0)
 *   .build()
 * // chromosome: NumericChromosome[Int, IntGene] = NumericChromosome(Seq(IntGene(12), IntGene(20), ...))
 * }}}
 */
trait NumericChromosomeBuilder[T, G <: NumericGene[T, G]] extends ChromosomeBuilder[T, G] {

    /** Stores the ranges used to constrain the values of genes in the chromosome. */
    private val _ranges: ListBuffer[(T, T)] = ListBuffer.empty

    /** Stores the filters used to constrain the values of genes in the chromosome. */
    private val _filters: ListBuffer[T => Boolean] = ListBuffer.empty

    /** Returns the sequence of ranges currently set for this builder.
     *
     * @return A sequence of ranges defining the bounds for gene values in the chromosome.
     */
    final def ranges: Seq[(T, T)] = _ranges.toSeq

    /** Returns the sequence of filters currently set for this builder.
     *
     * @return A sequence of filters to apply to the genes in the chromosome.
     */
    final def filters: Seq[T => Boolean] = _filters.toSeq

    /** Adds a range to the builder and returns this instance for method chaining.
     *
     * The `addRange` method allows you to specify a range for the values of genes in the chromosome. Multiple ranges
     * can be added, with each corresponding to a gene in the chromosome.
     *
     * @param range A tuple defining the minimum and maximum values for a gene.
     * @return This `NumericChromosomeBuilder` instance for method chaining.
     */
    final def addRange(range: (T, T)): this.type = {
        _ranges += range
        this
    }

    /** Adds a filter to the builder and returns this instance for method chaining.
     *
     * The `addFilter` method allows you to specify a filter that each gene's value must satisfy. Multiple filters can
     * be added, with each corresponding to a gene in the chromosome.
     *
     * @param filter A predicate function that returns `true` if a gene's value is valid, `false` otherwise.
     * @return This `NumericChromosomeBuilder` instance for method chaining.
     */
    final def addFilter(filter: T => Boolean): this.type = {
        _filters += filter
        this
    }

    /** Returns the default range to be used if no specific range is set.
     *
     * This method must be implemented by subclasses to provide a default range for gene values.
     *
     * @return A tuple defining the default minimum and maximum values for a gene.
     */
    protected def defaultRange: (T, T)

    /** Returns the default filter to be used if no specific filter is set.
     *
     * The default implementation returns a filter that always returns `true`, meaning all values are accepted.
     *
     * @return A predicate function that returns `true` for all values.
     */
    protected def defaultFilter: T => Boolean = _ => true

    /** Builds and returns a [[Chromosome]] instance.
     *
     * The `build` method creates a `NumericChromosome` using the current configuration of the builder. It enforces
     * constraints on the ranges and filters to ensure they match the chromosome size, and then uses the
     * `createChromosome` method to generate the chromosome.
     *
     * @return A new `Chromosome` instance.
     * @throws InvalidSizeException if the chromosome size is not set.
     */
    override final def build()(using random: Random): Chromosome[T, G] = {
        constrained {
            "Size should not be None" ~ (size mustNot BeNone, InvalidSizeException(_))
        }
        ChromosomeUtils.enforceConstraints(_ranges, _filters, size.get)
        val (ranges, filters) =
            ChromosomeUtils.adjustRangesAndFilters(size.get, _ranges, defaultRange, _filters, defaultFilter)
        createChromosome(ranges.toSeq, filters.toSeq)
    }

    /** Creates a chromosome with the specified ranges and filters.
     *
     * This method must be implemented by subclasses to provide the logic for creating a chromosome using the adjusted
     * ranges and filters.
     *
     * @param ranges  A sequence of ranges defining the bounds for gene values.
     * @param filters A sequence of filters to apply to the genes.
     * @return A new `Chromosome` instance with the specified ranges and filters.
     */
    protected def createChromosome(
        ranges: Seq[(T, T)],
        filters: Seq[T => Boolean]
    )(using random: Random): Chromosome[T, G]
}
