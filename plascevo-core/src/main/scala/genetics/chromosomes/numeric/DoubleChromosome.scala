package cl.ravenhill.plascevo
package genetics.chromosomes.numeric

import genetics.chromosomes.Chromosome
import genetics.genes.numeric.DoubleGene

/** Represents a chromosome composed of `DoubleGene` instances in an evolutionary algorithm.
 *
 * The `DoubleChromosome` class extends the [[Chromosome]] trait and encapsulates a sequence of `DoubleGene` instances,
 * which represent the genes of this chromosome. This class is used in evolutionary algorithms where the genes are
 * numeric values of type `Double`.
 *
 * @param genes A sequence of `DoubleGene` instances that make up this chromosome.
 * @example
 * {{{
 * // Example usage of DoubleChromosome
 * val genes = Seq(DoubleGene(0.5), DoubleGene(1.2), DoubleGene(3.8))
 * val chromosome = DoubleChromosome(genes)
 * // chromosome: DoubleChromosome = DoubleChromosome(Seq(DoubleGene(0.5), DoubleGene(1.2), DoubleGene(3.8)))
 *
 * // Access the genes by index
 * val firstGene = chromosome(0)
 * // firstGene: DoubleGene = DoubleGene(0.5)
 *
 * // Flatten the chromosome to get a sequence of gene values
 * val flattened = chromosome.flatten()
 * // flattened: Seq[Double] = Seq(0.5, 1.2, 3.8)
 * }}}
 */
final case class DoubleChromosome(genes: Seq[DoubleGene]) extends NumericChromosome[Double, DoubleGene] {

    /** Creates a new `DoubleChromosome` with a different set of genes.
     *
     * The `duplicateWithGenes` method allows you to create a new `DoubleChromosome` by providing a new sequence of
     * `DoubleGene` instances. This method is useful when you need to modify the genes of the chromosome while
     * preserving its type.
     *
     * @param genes The new sequence of `DoubleGene` instances for the duplicated chromosome.
     * @return A new `DoubleChromosome` with the specified genes.
     */
    override def duplicateWithGenes(genes: Seq[DoubleGene]): DoubleChromosome = DoubleChromosome(genes)
}
