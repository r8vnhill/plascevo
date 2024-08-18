package cl.ravenhill.plascevo
package genetics.chromosomes

import ToStringMode.SIMPLE
import genetics.genes.BooleanGene
import utils.{roundUpToMultipleOf, toInt}

private val chunkSize = 4

/** A case class representing a chromosome composed of boolean genes in a genetic algorithm.
 *
 * The `BooleanChromosome` class extends the `Chromosome` trait, specifically designed to hold a sequence of
 * `BooleanGene` instances. It provides methods for duplicating the chromosome with a new set of genes and converting
 * the chromosome to a string representation based on the current `toStringMode` in the `Domain`.
 *
 * @param genes The sequence of `BooleanGene` instances that make up the chromosome.
 */
case class BooleanChromosome(genes: Seq[BooleanGene]) extends Chromosome[Boolean, BooleanGene](genes) {

    /** Duplicates the chromosome with a new set of boolean genes.
     *
     * This method creates a new instance of `BooleanChromosome` with the specified sequence of genes.
     *
     * @param genes The sequence of `BooleanGene` instances to be used in the new chromosome.
     * @return A new `BooleanChromosome` instance with the specified genes.
     */
    override def duplicateWithGenes(genes: Seq[BooleanGene]): BooleanChromosome = new BooleanChromosome(genes)

    /** Converts the chromosome to a string representation based on the current `toStringMode` in the `Domain`.
     *
     * This method provides two string representations:
     * - `SIMPLE`: Represents the chromosome as a binary string, padded with zeroes to a length that is a multiple of
     * `chunkSize`, and grouped into chunks.
     * - Default: Provides a detailed string representation of the chromosome, listing all genes.
     *
     * @return A string representation of the chromosome.
     */
    override def toString: String = Domain.toStringMode match {
        case SIMPLE => {
            val stringSize = size roundUpToMultipleOf chunkSize
            val paddingZeroes = "0" * (stringSize - size)
            (paddingZeroes + genes.map(_.value.toInt).mkString)
                .grouped(chunkSize)
                .mkString(" ")
        }
        case _ => s"BooleanChromosome(${genes.mkString(", ")})"
    }
}
