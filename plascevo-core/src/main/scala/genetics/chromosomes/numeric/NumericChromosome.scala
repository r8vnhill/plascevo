package cl.ravenhill.plascevo
package genetics.chromosomes.numeric

import genetics.chromosomes.Chromosome
import genetics.genes.numeric.NumericGene

/** Represents a chromosome composed of numeric genes in an evolutionary algorithm.
 *
 * The `NumericChromosome` trait extends the [[Chromosome]] trait and serves as a base for chromosomes where the genes
 * are numeric in nature, represented by [[NumericGene]]. This trait is typically used in evolutionary algorithms that
 * operate on numeric data, enabling specialized operations and transformations specific to numeric chromosomes.
 *
 * @tparam T The type of numeric value stored by the genes within the chromosome.
 * @tparam G The type of gene contained in the chromosome, which must extend [[NumericGene]].
 *
 * @example
 * {{{
 * // Example usage of a NumericChromosome with integer genes
 * class IntegerChromosome(override val genes: Seq[IntGene]) extends NumericChromosome[Int, IntGene] {
 *     override def duplicateWithGenes(genes: Seq[IntGene]): IntegerChromosome = new IntegerChromosome(genes)
 *     // Other methods and properties
 * }
 *
 * val genes = Seq(IntGene(1), IntGene(2), IntGene(3))
 * val chromosome = new IntegerChromosome(genes)
 * // chromosome: IntegerChromosome = IntegerChromosome(Seq(IntGene(1), IntGene(2), IntGene(3)))
 * }}}
 */
trait NumericChromosome[T, G <: NumericGene[T, G]] extends Chromosome[T, G]
